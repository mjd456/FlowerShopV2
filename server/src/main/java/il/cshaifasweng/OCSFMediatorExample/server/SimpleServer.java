package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import jakarta.mail.MessagingException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.LockModeType;


public class SimpleServer extends AbstractServer {
	private static final List<SubscribedClient> SubscribersList = new CopyOnWriteArrayList<>();
	private static SessionFactory sessionFactory;
	private static Session session;
	private final ScheduledExecutorService orderDeliverySweeper = Executors.newSingleThreadScheduledExecutor();
	private final ZoneId sweeperZone = ZoneId.of("Asia/Jerusalem"); // or ZoneId.systemDefault()

	public SimpleServer(int port) {
		super(port);
		initializeSession();
		startOrderDeliverySweeper();
		Runtime.getRuntime().addShutdownHook(new Thread(this::stopOrderDeliverySweeper));

		DailyTaskScheduler.scheduleDailyAtTime(0, 0, SimpleServer::expireAllSubscriptionsAndNotifyLoggedClients);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received: " + msg.getClass());

		if (msg instanceof String msgString) {
			if (msgString.startsWith("#warning")) {
				Warning warning = new Warning("Warning from server!");
				try {
					client.sendToClient(warning);
					System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (msgString.startsWith("GetAccounts")){
				List<Account> accounts = new LinkedList<>();
				try (Session session = sessionFactory.openSession()) {
					session.beginTransaction();

					Query<Account> query = session.createQuery(
							"FROM Account", Account.class);

					accounts = query.getResultList();
					session.getTransaction().commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					client.sendToClient(accounts);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else if (msgString.startsWith("add client")) {
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				try {
					client.sendToClient("client added successfully");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else if (msgString.startsWith("remove client")) {
				if (!SubscribersList.isEmpty()) {
					for (SubscribedClient subscribedClient : SubscribersList) {
						if (subscribedClient.getClient().equals(client)) {
							SubscribersList.remove(subscribedClient);
							break;
						}
					}
				}
			}
			else if (msgString.startsWith("RefreshList")) {
				sendToClientRefreshedList(client);
			}
			else if (msgString.startsWith("Does this email exist?")) {
				String[] tokens = msgString.split("\\s+");

				String emailToCheck = tokens[4];

				try (Session session = sessionFactory.openSession()) {
					session.beginTransaction();

					Query<Account> query = session.createQuery(
							"FROM Account WHERE email = :email", Account.class);
					query.setParameter("email", emailToCheck);

					Account found = query.uniqueResult();

					if (found != null) {
						System.out.println("Email exists in the database: " + emailToCheck);

						// Generate 6-digit code
						String code = generate6DigitCode();

						// Store the code in the DB (if you're using PasswordResetCode entity)
						PasswordResetCode resetCode = new PasswordResetCode(found, code, getExpiryTimestamp());
						session.save(resetCode);

						// Send the email
						EmailSender.sendEmail(
								emailToCheck,
								"Your Password Reset Code",
								"Hello " + found.getFirstName() + ",\n\nYour password reset code is: " + code + "\n\n" +
										"If you did not request a reset, please ignore this email."
						);
						client.sendToClient("Email for recovery found " + emailToCheck);
					} else {
						System.out.println("Email does not exist: " + emailToCheck);
						client.sendToClient("Email for recovery not found");
					}
					session.getTransaction().commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (msgString.startsWith("Is the code correct?")) {
				String[] tokens = msgString.split("\\s+");

				if (tokens.length != 6) {
					try {
						client.sendToClient("Invalid verification code.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				String code = tokens[4];
				String emailToCheck = tokens[5];

				try (Session session = sessionFactory.openSession()) {
					session.beginTransaction();

					// Get the user
					Query<Account> userQuery = session.createQuery(
							"FROM Account WHERE email = :email", Account.class);
					userQuery.setParameter("email", emailToCheck);
					Account account = userQuery.uniqueResult();

					if (account == null) {
						client.sendToClient("No such email exists.");
						return;
					}

					// Get latest reset code for user
					Query<PasswordResetCode> codeQuery = session.createQuery(
							"FROM PasswordResetCode WHERE user = :user ORDER BY expiresAt DESC", PasswordResetCode.class);
					codeQuery.setParameter("user", account);
					codeQuery.setMaxResults(1);
					PasswordResetCode latestCode = codeQuery.uniqueResult();

					if (latestCode == null) {
						client.sendToClient("No reset code found.");
					} else {
						Date now = new Date();

						if (now.after(latestCode.getExpiresAt())) {
							// Code expired → generate a new one and resend
							String newCode = generate6DigitCode();
							PasswordResetCode newResetCode = new PasswordResetCode(account, newCode, getExpiryTimestamp());
							session.save(newResetCode);

							EmailSender.sendEmail(
									emailToCheck,
									"Your New Password Reset Code",
									"Your previous code expired. Here's a new one: " + newCode
							);

							client.sendToClient("The code has expired. A new code was sent to " + emailToCheck);
						} else if (!latestCode.getCode().equals(code)) {
							client.sendToClient("Invalid verification code.");
						} else {
							client.sendToClient("Code verified successfully.");
						}
					}

					session.getTransaction().commit();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						client.sendToClient("Server error during code verification.");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			else if (msgString.startsWith("Is this password unique?")) {
				String[] tokens = msgString.split("\\s+");
				if (tokens.length < 6) {
					try {
						client.sendToClient("Changing password error : Invalid password uniqueness check request.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				String newPassword = tokens[4];
				String emailToCheck = tokens[5];

				try (Session session = sessionFactory.openSession()) {
					session.beginTransaction();

					Query<Account> accountQuery = session.createQuery(
							"FROM Account WHERE email = :email", Account.class);
					accountQuery.setParameter("email", emailToCheck);
					Account account = accountQuery.uniqueResult();

					if (account == null) {
						client.sendToClient("Changing password error : Account not found.");
						session.getTransaction().commit();
						return;
					}

					Query<PasswordHistory> historyQuery = session.createQuery(
							"FROM PasswordHistory WHERE user = :user AND passwordHash = :pass", PasswordHistory.class);
					historyQuery.setParameter("user", account);
					historyQuery.setParameter("pass", newPassword); // or hashedPassword

					if (!historyQuery.getResultList().isEmpty()) {
						client.sendToClient("Changing password error : This password has already been used. Choose a new one.");
						session.getTransaction().commit();
						return;
					}

					Query<PasswordHistory> currentQuery = session.createQuery(
							"FROM PasswordHistory WHERE user = :user AND isCurrent = true", PasswordHistory.class);
					currentQuery.setParameter("user", account);
					List<PasswordHistory> currentEntries = currentQuery.getResultList();

					for (PasswordHistory entry : currentEntries) {
						entry.setCurrent(false);
						entry.setSwappedAt(new Date());
						session.update(entry);
					}

					account.setPassword(newPassword);
					session.update(account);

					PasswordHistory newHistory = new PasswordHistory();
					newHistory.setUser(account);
					newHistory.setPasswordHash(newPassword); // or hashedPassword
					newHistory.setCurrent(true);
					newHistory.setCreatedAt(new Date());
					session.save(newHistory);

					client.sendToClient("Password successfully changed.");
					session.getTransaction().commit();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						client.sendToClient("Changing password error : Error occurred while changing password.");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			else if (msgString.startsWith("RequestFlowerCatalogForManager")) {
				sendToClientRefreshedList(client);
			}
			else if (msgString.startsWith("Send all Feedbacks")) {
				Session session = sessionFactory.openSession();
				List<FeedBackSQL> allFeedbacks = new ArrayList<>();
				try {
					allFeedbacks = session.createQuery("FROM FeedBackSQL", FeedBackSQL.class).getResultList();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					session.close();
				}

				try {
					client.sendToClient(new GetAllFeedbacksResponse(allFeedbacks));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else if (msg instanceof OrderSQL orderSQL) {
			System.out.println("Received OrderSQL from client: " + orderSQL.getDetails());

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				// Fetch the account by ID to attach
				Account managedAccount = session.get(Account.class, orderSQL.getAccount().getId());
				if (managedAccount == null) {
					System.err.println("Account not found for OrderSQL: " + orderSQL.getAccount().getId());
					tx.rollback();
					return;
				}

				orderSQL.setAccount(managedAccount);

				session.save(orderSQL);
				tx.commit();
				client.sendToClient(new OrderSavedConfirmation());
				System.out.println("OrderSQL saved successfully.");
				client.sendToClient("Order saved successfully");
				List<OrderSQL> orders = session.createQuery(
								"FROM OrderSQL WHERE account.id = :id ORDER BY deliveryDate DESC", OrderSQL.class)
						.setParameter("id", managedAccount.getId())
						.getResultList();

				client.sendToClient(new GetUserOrdersResponse(orders));

			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof LoginRequest loginRequest) {
			String email = loginRequest.getUsername();
			String password = loginRequest.getPassword();

			System.out.println("Received login request from client:");
			System.out.println("email: " + email);
			System.out.println("Password: " + password);

			Account account = null;

			try (Session session = sessionFactory.openSession()) {
				session.beginTransaction();

				Query<Account> query = session.createQuery(
						"FROM Account WHERE email = :email AND password = :password", Account.class
				);
				query.setParameter("email", email);
				query.setParameter("password", password);

				List<Account> results = query.getResultList();

				session.getTransaction().commit();

				if (!results.isEmpty()) {
					account = results.get(0);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (account == null) {
					// Account not found -> deny login
					client.sendToClient(new LoginResponse(false, -1, "-", null));
				} else if (account.isLogged()) {
					// Already logged in -> deny login
					client.sendToClient(new LoginResponse(false, -1, "-", null));
				} else {
					// Not logged in -> mark as logged in and update DB
					try (Session session = sessionFactory.openSession()) {
						Transaction tx = session.beginTransaction();

						Account accountInDb = session.get(Account.class, account.getId()); // safer
						accountInDb.setLogged(true); // set the flag on the managed object

						tx.commit();
						Account fresh = session.get(Account.class, accountInDb.getId());

						// Send success response
						client.sendToClient(new LoginResponse(
								true,
								accountInDb.getId(),
								accountInDb.getFirstName(),
								accountInDb
						));

						SubscribedClient sub = getSubscribedClientByConnection(client);
						if (sub == null) {
							// Not present, add new - to avoid bugs
							sub = new SubscribedClient(client, fresh);
							SubscribersList.add(sub);
						} else {
							// Already present, update account
							sub.setAccount(fresh);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof SignUpRequest signupDetails) {
			System.out.println("Received SignUp request from client");

			Session session = null;

			try {
				session = sessionFactory.openSession();
				session.beginTransaction();

				Query<Account> query = session.createQuery("FROM Account WHERE email = :email", Account.class);
				query.setParameter("email", signupDetails.getEmail());

				List<Account> existing = query.getResultList();

				if (!existing.isEmpty()) {
					System.out.println("Email already exists: " + signupDetails.getEmail());
					client.sendToClient("Email already exists");
				} else {
					Account newAccount = new Account();
					newAccount.setEmail(signupDetails.getEmail());
					newAccount.setPassword(signupDetails.getPassword());
					newAccount.setFirstName(signupDetails.getFirstName());
					newAccount.setLastName(signupDetails.getLastName());
					newAccount.setIdentityNumber(signupDetails.getIdentityNumber());
					newAccount.setCreditCardNumber(signupDetails.getCreditCardNumber());
					newAccount.setCvv(signupDetails.getCvv());
					newAccount.setCreditCardValidUntil(signupDetails.getCreditCardValidUntil());
					newAccount.setPhoneNumber(signupDetails.getPhoneNumber());
					newAccount.setSubscribtion_level("Free");
					newAccount.setAccountLevel("Customer");

					session.save(newAccount);
					session.flush();

					PasswordHistory history = new PasswordHistory();
					history.setUser(newAccount);
					history.setPasswordHash(newAccount.getPassword()); // or hash if applicable
					history.setCurrent(true);
					history.setCreatedAt(new Date());
					session.save(history);
					session.getTransaction().commit();

					try {
						EmailSender.sendEmail(
								signupDetails.getEmail(),
								"Sign Up Successful",
								"Hey "+ signupDetails.getFirstName() + "," + "\nWe are happy to have you as a customer."
						);
						System.out.println("Sign-up succeeded for email: " + signupDetails.getEmail());
						client.sendToClient("Sign-up succeeded for email");
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
		else if (msg instanceof GetUserOrdersRequest req) {
			int accountId = req.getAccountId();
			List<OrderSQL> orders = null;

			try (Session session = sessionFactory.openSession()) {
				orders = session.createQuery(
								"FROM OrderSQL WHERE account.id = :id ORDER BY deliveryDate DESC", OrderSQL.class)
						.setParameter("id", accountId)
						.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				client.sendToClient(new GetUserOrdersResponse(orders));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof UpdateFlowerRequest updateRequest) {
			Flower updatedData = updateRequest.getFlower();
			Session session = null;
			Transaction tx = null;
			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				Flower flowerInDb = session.get(Flower.class, updatedData.getId());

				if (flowerInDb != null) {
					String oldName = flowerInDb.getName();
					String newName = updatedData.getName();

					// 1) Basic fields
					flowerInDb.setName(newName);
					flowerInDb.setPrice(updatedData.getPrice());
					flowerInDb.setDescription(updatedData.getDescription());
					flowerInDb.setColor(updatedData.getColor());

					// 2) Store-specific supplies (defensive: no negatives)
					int haifa   = Math.max(0, updatedData.getSupplyHaifa());
					int eilat   = Math.max(0, updatedData.getSupplyEilat());
					int telAviv = Math.max(0, updatedData.getSupplyTelAviv());

					flowerInDb.setSupplyHaifa(haifa);
					flowerInDb.setSupplyEilat(eilat);
					flowerInDb.setSupplyTelAviv(telAviv);

					// 3) Recompute total supply = sum of stores
					int total = haifa + eilat + telAviv;
					flowerInDb.setSupply(total);

					// Persist flower
					session.update(flowerInDb);

					// 4) Keep existing “rename in orders.details” logic
					List<OrderSQL> affectedOrders = session.createQuery(
									"FROM OrderSQL WHERE details LIKE :oldName", OrderSQL.class)
							.setParameter("oldName", "%" + oldName + "%")
							.list();

					for (OrderSQL order : affectedOrders) {
						String details = order.getDetails();
						String updatedDetails = updateFlowerNameInDetails(details, oldName, newName);
						if (!updatedDetails.equals(details)) {
							order.setDetails(updatedDetails);
							session.update(order);
						}
					}

					tx.commit();
					System.out.println("Currently " + SubscribersList.size() + " subscribed clients.");

					// Notify all clients
					UpdateFlowerNotification notification = new UpdateFlowerNotification(flowerInDb);
					sendToAllClients(notification);
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof LogoutRequest logoutRequest) {
			System.out.println("Logout request from client");

			Account requestAccount = logoutRequest.getAccount();
			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				// Re-fetch the account using the ID from the client
				Account accountInDb = session.get(Account.class, requestAccount.getId());

				if (accountInDb != null) {
					accountInDb.setLogged(false);
					try {
						client.sendToClient("Logout successful");
					} catch (IOException e) {
						System.err.println("Client disconnected during send.");
						// Do not crash, just remove from list
					}

					SubscribedClient toRemove = null;
					for (SubscribedClient sub : SubscribersList) {
						if (sub.getClient().equals(client)) {
							toRemove = sub;
							break;
						}
					}
					if (toRemove != null) {
						SubscribersList.remove(toRemove);
					}
				} else {
					System.err.println("Account not found in DB (LogoutRequest).");
				}

				tx.commit(); // Always commit the logout, even if client gone
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof Feedback) {
			Feedback feedbackMsg = (Feedback) msg;

			Session session = sessionFactory.openSession();
			Transaction tx = null;

			FeedBackSQL feedbackEntity = null;

			try {
				tx = session.beginTransaction();

				// Always fetch the Account from the DB using the ID sent from the client
				int accountId = feedbackMsg.getAccount().getId();
				Account account = session.get(Account.class, accountId);
				if (account == null) {
					client.sendToClient("Error: Account not found in database.");
					tx.rollback();
					return;
				}

				// Create the feedback entity with the managed Account instance
				feedbackEntity = new FeedBackSQL(
						account,
						feedbackMsg.getfeedbackTtitle(),
						feedbackMsg.getfeedbackTdesc()
				);

				session.save(feedbackEntity);

				// Eager-load account fields that will be needed by clients
				feedbackEntity.getAccount().getEmail();

				tx.commit();
				System.out.println("Feedback succeeded for account: " + accountId);
				client.sendToClient("Feedback added successfully");

				// SOFT UPDATE: Send only the new feedback to all customer service clients
				NewFeedbackNotification notif = new NewFeedbackNotification(feedbackEntity);
				sendToAllClients(notif);

			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient("Error adding feedback: " + e.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} finally {
				session.close();
			}
		}
		else if (msg instanceof GetUserFeedbacksRequest) {
			GetUserFeedbacksRequest req = (GetUserFeedbacksRequest) msg;
			int accountId = req.getAccountId();
			List<FeedBackSQL> feedbacks = null;

			Session session = sessionFactory.openSession();
			try {
				// Query all feedbacks for this account, order by submission time (newest first)
				feedbacks = session.createQuery(
								"FROM FeedBackSQL WHERE account.id = :id ORDER BY submittedAt DESC", FeedBackSQL.class)
						.setParameter("id", accountId)
						.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
				// Optionally send error message to client here
			} finally {
				session.close();
			}

			// Wrap in response object and send to client
			try {
				client.sendToClient(new GetUserFeedbacksResponse(feedbacks));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof QuarterlyRevenueReportRequest req) {
			System.out.println("Received QuarterlyRevenueReportRequest for branch ID: " + req.getBranchId());

			try (Session s = sessionFactory.openSession()) {
				String hql = "SELECT YEAR(o.deliveryDate), QUARTER(o.deliveryDate), SUM(COALESCE(o.totalPrice, 0) - COALESCE(o.refundAmount, 0)) " +
						"FROM OrderSQL o " +
						"WHERE o.deliveryDate BETWEEN :from AND :to ";

				if (req.getBranchId() > 0) { // If a specific branch is requested
					hql += "AND o.branch.id = :branchId ";
				}

				hql += "GROUP BY YEAR(o.deliveryDate), QUARTER(o.deliveryDate) " +
						"ORDER BY YEAR(o.deliveryDate), QUARTER(o.deliveryDate)";

				Query<Object[]> query = s.createQuery(hql, Object[].class)
						.setParameter("from", req.getFrom())
						.setParameter("to", req.getTo());

				if (req.getBranchId() > 0) {
					query.setParameter("branchId", req.getBranchId());
				}

				List<Object[]> rows = query.list();
				List<QuarterlyRevenueReportResponse.Row> out = new ArrayList<>();
				for (Object[] r : rows) {
					int year    = ((Number) r[0]).intValue();
					int quarter = ((Number) r[1]).intValue();
					double rev  = r[2] != null ? ((Number) r[2]).doubleValue() : 0.0;
					out.add(new QuarterlyRevenueReportResponse.Row(year, quarter, rev));
				}

				client.sendToClient(new QuarterlyRevenueReportResponse(out));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof OrdersByProductTypeReportRequest req) {
			System.out.println("Received OrdersByProductTypeReportRequest for branch ID: " + req.getBranchId());

			try (Session s = sessionFactory.openSession()) {
				String hql = "FROM OrderSQL o WHERE o.deliveryDate BETWEEN :from AND :to ";

				if (req.getBranchId() > 0) {
					hql += "AND o.branch.id = :branchId";
				}

				Query<OrderSQL> query = s.createQuery(hql, OrderSQL.class)
						.setParameter("from", req.getFrom())
						.setParameter("to", req.getTo());

				if (req.getBranchId() > 0) {
					query.setParameter("branchId", req.getBranchId());
				}

				List<OrderSQL> orders = query.list();
				Map<String, long[]> aggregationMap = new HashMap<>();

				for (OrderSQL order : orders) {
					String details = order.getDetails();
					if (details == null || details.isBlank()) continue;

					String[] items = details.split(",");
					for (String item : items) {
						item = item.trim();
						int separatorIndex = item.lastIndexOf(" x");
						if (separatorIndex > 0) {
							String productType = item.substring(0, separatorIndex).trim();
							int quantity = Integer.parseInt(item.substring(separatorIndex + 2).trim());
							long[] values = aggregationMap.computeIfAbsent(productType, k -> new long[3]);
							values[0]++;
							values[1] += quantity;
						}
					}
				}

				List<OrdersByProductTypeReportResponse.Row> responseRows = new ArrayList<>();
				for (Map.Entry<String, long[]> entry : aggregationMap.entrySet()) {
					responseRows.add(new OrdersByProductTypeReportResponse.Row(entry.getKey(), entry.getValue()[0], entry.getValue()[1], 0.0));
				}

				client.sendToClient(new OrdersByProductTypeReportResponse(responseRows));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof ComplaintsHistogramReportRequest req) {
			System.out.println("Received ComplaintsHistogramReportRequest for branch ID: " + req.getBranchId());

			try (Session s = sessionFactory.openSession()) {
				String hql = "FROM FeedBackSQL f WHERE f.submittedAt BETWEEN :from AND :to ";

				if (req.getBranchId() > 0) {
					hql += "AND f.account.branch.id = :branchId";
				}

				Query<FeedBackSQL> query = s.createQuery(hql, FeedBackSQL.class)
						.setParameter("from", req.getFrom())
						.setParameter("to", req.getTo());

				if (req.getBranchId() > 0) {
					query.setParameter("branchId", req.getBranchId());
				}

				List<FeedBackSQL> complaints = query.list();
				System.out.println("[SERVER] Found " + complaints.size() + " complaints in the database.");

				Map<LocalDate, Long> countsByDay = new LinkedHashMap<>();
				for (FeedBackSQL complaint : complaints) {
					if (complaint.getSubmittedAt() != null) {
						LocalDate day = complaint.getSubmittedAt().toLocalDate();
						countsByDay.merge(day, 1L, Long::sum);
					}
				}

				client.sendToClient(new ComplaintsHistogramReportResponse(countsByDay));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof UpdateFeedbackStatusRequest) {
			UpdateFeedbackStatusRequest req = (UpdateFeedbackStatusRequest) msg;
			int feedbackId = req.getFeedbackId();
			FeedBackSQL.FeedbackStatus newStatus = req.getStatus();

			Session session = sessionFactory.openSession();
			Transaction tx = null;

			try {
				tx = session.beginTransaction();

				// Fetch feedback by ID
				FeedBackSQL feedback = session.get(FeedBackSQL.class, feedbackId);
				if (feedback != null) {
					feedback.setStatus(newStatus);
					feedback.setResolvedAt(LocalDateTime.now());
					session.update(feedback);
					tx.commit();

					// Optionally: Send updated feedbacks list back to client(s)
					List<FeedBackSQL> allFeedbacks = session.createQuery("FROM FeedBackSQL", FeedBackSQL.class).getResultList();
					client.sendToClient(new GetAllFeedbacksResponse(allFeedbacks));
				} else {
					client.sendToClient("Error: Feedback not found.");
					if (tx != null) tx.rollback();
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try { client.sendToClient("Error updating feedback: " + e.getMessage()); }
				catch (Exception ex) { ex.printStackTrace(); }
			} finally {
				session.close();
			}
		}
		else if (msg instanceof SubscriptionRequest) {
			SubscriptionRequest req = (SubscriptionRequest) msg;
			int accountId = req.getAccount().getId();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				Account account = session.get(Account.class, accountId);
				if (account == null) {
					client.sendToClient("Error: Account not found.");
					return;
				}

				account.setSubscribtion_level("Plus");

				LocalDate expiresAt = LocalDate.now().plusYears(1);
				Date expiresDate = java.sql.Date.valueOf(expiresAt);
				account.setSubscription_expires_at(expiresDate.toString());

				account.setAuto_renew_subscription("Yes");

				session.update(account);
				tx.commit();

				client.sendToClient(new AccountUpgradeResponse(account));
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try { client.sendToClient("Error upgrading subscription."); } catch (Exception ex) { ex.printStackTrace(); }
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof CancelAutoRenewRequest) {
			CancelAutoRenewRequest req = (CancelAutoRenewRequest) msg;
			int accountId = req.getAccount().getId();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				Account account = session.get(Account.class, accountId);
				if (account == null) {
					client.sendToClient("Error: Account not found.");
					return;
				}

				// Check if auto-renew is 'Yes'
				if ("Yes".equalsIgnoreCase(account.getAuto_renew_subscription())) {
					account.setAuto_renew_subscription("No");
					session.update(account);
					tx.commit();
					client.sendToClient(new AutoRenewDisableResponse(account));
				} else {
					tx.commit(); // Still commit if nothing changed, for consistency
					client.sendToClient("Auto-renew was already disabled.");
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient("Error canceling auto-renew.");
				} catch (Exception ex) { ex.printStackTrace(); }
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof DeleteFlowerRequest delReq) {
			int idToDelete = delReq.getFlowerId();
			try (Session session = sessionFactory.openSession()) {
				Transaction tx = session.beginTransaction();
				Flower flower = session.get(Flower.class, idToDelete);
				if (flower != null) {
					session.delete(flower);
					tx.commit();
					System.out.println("Flower deleted: " + flower.getName());
					sendToAllClients(new FlowerDeleted(flower.getId()));
						// Optionally send a refreshed list or a notification to all clients
				} else {
					tx.rollback();
					System.out.println("Flower to delete not found");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// Optionally: send back a confirmation or updated catalog
		}
		else if (msg instanceof AddFlowerRequest addFlowerRequest) {
			System.out.println("Received AddFlowerRequest from client.");

			Flower newFlower = addFlowerRequest.getNewFlower();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				session.save(newFlower); // Save to DB, ID assigned

				tx.commit();
				System.out.println("New flower added to DB: " + newFlower.getName());

				// Notify the requesting client
				client.sendToClient("Flower added successfully");

				// Notify all clients (including sender) about the new flower
				NewFlowerNotification notification = new NewFlowerNotification(newFlower);
				for (SubscribedClient sub : SubscribersList) {
					try {
						sub.getClient().sendToClient(notification);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient("Failed to add flower: " + e.getMessage());
				} catch (Exception ex) { ex.printStackTrace(); }
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof UpdatePasswordRequest req) {
			// Find the Account associated with this ConnectionToClient
			Account account = null;
			for (SubscribedClient sub : SubscribersList) {
				if (sub.getClient().equals(client)) {
					account = sub.getAccount();
					break;
				}
			}
			if (account == null) {
				try {
					client.sendToClient(new UpdatePasswordResponse(false, "Session error: account not found."));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			String newPassword = req.getNewPass();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				// 1. Check if password was ever used before for this user
				Query<PasswordHistory> q = session.createQuery(
						"FROM PasswordHistory WHERE user = :user AND passwordHash = :password", PasswordHistory.class
				);
				q.setParameter("user", account);
				q.setParameter("password", newPassword);

				if (!q.getResultList().isEmpty()) {
					client.sendToClient(new UpdatePasswordResponse(false, "You cannot reuse an old password."));
					tx.rollback();
					return;
				}

				// 2. Set previous current passwords to isCurrent = false
				Query<PasswordHistory> currQ = session.createQuery(
						"FROM PasswordHistory WHERE user = :user AND isCurrent = true", PasswordHistory.class
				);
				currQ.setParameter("user", account);
				for (PasswordHistory ph : currQ.getResultList()) {
					ph.setCurrent(false);
					ph.setSwappedAt(Calendar.getInstance().getTime());
					session.update(ph);
				}

				// 3. Update account password
				Account managedAccount = session.get(Account.class, account.getId());
				managedAccount.setPassword(newPassword);
				session.update(managedAccount);

				// 4. Save new password in PasswordHistory
				PasswordHistory newHist = new PasswordHistory(
						managedAccount, newPassword, true, new Date()
				);
				session.save(newHist);

				tx.commit();
				client.sendToClient(new UpdatePasswordResponse(true, "Password updated successfully."));

			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient(new UpdatePasswordResponse(false, "Server error during password update."));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof UpdateCreditCardRequest req) {
			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				// Find account by ID
				Account account = session.get(Account.class, req.getAccountId());
				if (account == null) {
					client.sendToClient("Account not found");
					tx.rollback();
					return;
				}

				// Update credit card details
				account.setCreditCardNumber(req.getCreditCardNumber());
				account.setCvv(req.getCcv());
				account.setCreditCardValidUntil(req.getValidUntil());

				session.update(account);
				tx.commit();

				// You can send a success response or event to the client if needed
				client.sendToClient(new UpdateCreditCardResponse(true, "Credit card updated.",account));
				// (Optional) Update SubscribedClient if you're storing an in-memory account object
				for (SubscribedClient sub : SubscribersList) {
					if (sub.getAccount().getId() == account.getId()) {
						sub.setAccount(account);
						break;
					}
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient(new UpdateCreditCardResponse(false, "Failed to update credit card info.",null));
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof CancelOrderRequest request) {
			long orderId = request.getOrderId();

			SubscribedClient subscribedClient = null;
			for (SubscribedClient sc : SubscribersList) {
				if (sc.getClient() == client) {
					subscribedClient = sc;
					break;
				}
			}

			if (subscribedClient == null || subscribedClient.getAccount() == null) {
				try {
					client.sendToClient(new CancelOrderResponse(orderId, false, "Client account not found or not logged in."));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return;
			}

			Account clientAccount = subscribedClient.getAccount();

			Session session = null;
			Transaction tx = null;
			try {
				session = sessionFactory.openSession();
				OrderSQL order = session.get(OrderSQL.class, orderId);

				if (order == null) {
					client.sendToClient(new CancelOrderResponse(orderId, false, "Order not found."));
					return;
				}

				if (order.getAccount().getId() != clientAccount.getId()) {
					client.sendToClient(new CancelOrderResponse(orderId, false, "You are not authorized to cancel this order."));
					return;
				}

				if ("canceled".equalsIgnoreCase(order.getStatus())) {
					client.sendToClient(new CancelOrderResponse(orderId, false, "Order already canceled."));
					return;
				}

				List<Flower> restockedFlowers = new ArrayList<>();

				tx = session.beginTransaction();

				double refundAmount = 0.0;
				double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice() : 0.0;
				String refundMsg;

				Date date = order.getDeliveryDate();
				LocalDate deliveryDate;
				if (date instanceof java.sql.Date) {
					deliveryDate = ((java.sql.Date) date).toLocalDate();
				} else {
					deliveryDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				}
				LocalTime deliveryTime = LocalTime.parse(order.getDeliveryTime());
				LocalDateTime deliveryDateTime = LocalDateTime.of(deliveryDate, deliveryTime);
				LocalDateTime now = LocalDateTime.now();

				long minutesBeforeDelivery = Duration.between(now, deliveryDateTime).toMinutes();

				if (minutesBeforeDelivery >= 180) {
					refundAmount = orderTotal;
					refundMsg = "Your order was cancelled. According to shop policy, you are entitled to a full refund.";
				} else if (minutesBeforeDelivery >= 60) {
					refundAmount = orderTotal * 0.5;
					refundMsg = "Your order was cancelled. According to shop policy, you are entitled to a 50% refund.";
				} else {
					refundAmount = 0.0;
					refundMsg = "Your order was cancelled. According to shop policy, no refund is given for cancellation within 1 hour of delivery.";
				}

				// --- Restock flowers ---
				String details = order.getDetails(); // e.g., "Rose x3, Lily x2,"
				if (details != null && !details.isBlank()) {
					String[] parts = details.split(",");
					for (String part : parts) {
						part = part.trim();
						if (part.isEmpty()) continue;

						// Expected format: "Rose x3"
						int xIdx = part.lastIndexOf("x");
						if (xIdx > 0) {
							String name = part.substring(0, xIdx).trim();
							String qtyStr = part.substring(xIdx + 1).replaceAll("[^\\d]", "").trim();
							int qty = qtyStr.isEmpty() ? 1 : Integer.parseInt(qtyStr);

							// Find the flower by name
							Query<Flower> flowerQuery = session.createQuery("from Flower where name = :name", Flower.class);
							flowerQuery.setParameter("name", name);
							Flower flower = flowerQuery.uniqueResult();
							if (flower != null) {
								flower.setSupply(flower.getSupply() + qty);
								session.update(flower);
								restockedFlowers.add(flower);
							}
						}
					}
				}
				order.setStatus("canceled");
				order.setRefundAmount(refundAmount);
				session.update(order);

				tx.commit();

				String msgToClient = String.format("%s Refund amount: ₪%.2f", refundMsg, refundAmount);
				client.sendToClient(new CancelOrderResponse(orderId, true, msgToClient));

				try {
					String toEmail = clientAccount.getEmail();
					String subject = "Order Cancellation Notification";
					String body = String.format(
							"Dear %s %s,\n\nYour order #%d was cancelled.\n\n%s\n\nRefund amount: ₪%.2f\n\nThank you for shopping with us.\n\nBest regards,\nFlower Shop",
							clientAccount.getFirstName(),
							clientAccount.getLastName(),
							orderId,
							refundMsg,
							refundAmount
					);
					EmailSender.sendEmail(toEmail, subject, body);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (!restockedFlowers.isEmpty()) {
					FlowersRestockedResponse restockedResponse = new FlowersRestockedResponse(restockedFlowers);
					sendToAllClients(restockedResponse);
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				try {
					client.sendToClient(new CancelOrderResponse(orderId, false, "Error while cancelling order."));
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
				e.printStackTrace();
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof PlaceOrderRequest request) {
			Session session = sessionFactory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();

				// 1. Check supply for real flowers only
				for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
					Flower clientFlower = entry.getKey();
					int qty = entry.getValue();

					if (clientFlower.getId() == 0) {
						// Custom item → skip DB lookup (no supply to check)
						continue;
					}

					Flower flower = session.get(Flower.class, clientFlower.getId()); // get fresh from DB
					if (flower == null) {
						tx.rollback();
						client.sendToClient(new PlaceOrderResponse(false,
								"Flower not found: " + clientFlower.getName()));
						return;
					}
					if (flower.getSupply() < qty) {
						tx.rollback();
						client.sendToClient(new PlaceOrderResponse(false,
								"Not enough supply for " + flower.getName()));
						return;
					}
				}

				// 2. Update supply & popularity for real flowers only
				for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
					Flower clientFlower = entry.getKey();
					int qty = entry.getValue();

					if (clientFlower.getId() == 0) {
						// Custom item → no supply update
						continue;
					}

					Flower flower = session.get(Flower.class, clientFlower.getId());
					flower.setSupply(flower.getSupply() - qty);
					flower.setPopularity(flower.getPopularity() + 1);
					session.update(flower);
				}

				// 3. Save order to DB
				Account managedAccount = session.get(Account.class, request.getCustomer().getId());
				if (managedAccount == null) {
					tx.rollback();
					client.sendToClient(new PlaceOrderResponse(false, "Account not found!"));
					return;
				}

				// Build details string
				StringBuilder details = new StringBuilder();
				for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
					details.append(entry.getKey().getName())
							.append(" x")
							.append(entry.getValue())
							.append(", ");
				}
				if (details.length() > 2) {
					details.setLength(details.length() - 2);
				}

				OrderSQL orderSQL = new OrderSQL(
						managedAccount,
						java.sql.Date.valueOf(request.getDate()),
						request.getTime(),
						request.getStatus(),
						details.toString(),
						request.getTotalPrice(),
						request.getAddressOrPickup(),
						request.getGreeting()
				);
				orderSQL.setAccount(managedAccount);

				session.save(orderSQL);
				tx.commit();

				client.sendToClient(new PlaceOrderResponse(true,
						"Order placed and saved successfully!"));
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient(new PlaceOrderResponse(false, "Error processing order."));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} finally {
				session.close();
			}
		}
		else if (msg instanceof Account){
			Account account = (Account) msg;
			Session session = null;
			Transaction tx = null;
			List<Account> accounts = new ArrayList<>();
			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				Account oldAccount = session.get(Account.class, account.getId());

				if (oldAccount != null) {
					oldAccount.setFirstName(account.getFirstName());
					oldAccount.setLastName(account.getLastName());
					oldAccount.setEmail(account.getEmail());
					oldAccount.setIdentityNumber(account.getIdentityNumber());
					oldAccount.setPassword(account.getPassword());
					oldAccount.setCreditCardValidUntil(account.getCreditCardValidUntil());
					oldAccount.setCreditCardNumber(account.getCreditCardNumber());
					oldAccount.setPhoneNumber(account.getPhoneNumber());
					session.update(oldAccount);

					tx.commit();
					System.out.println("Currently " + SubscribersList.size() + " subscribed clients.");
					tx = session.beginTransaction();
					Query<Account> query = session.createQuery(
							"FROM Account", Account.class);

					accounts = query.getResultList();
					sendToAllClients(accounts);
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
			} finally {
				if (session != null) session.close();
			}
		}
		else if (msg instanceof GetAllBranchesRequest) {
			try (Session s = sessionFactory.openSession()) {
				List<Branch> branches = s.createQuery("FROM Branch", Branch.class).list();
				client.sendToClient(new GetAllBranchesResponse(branches));
				System.out.println("Sent list of all branches to client.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Unhandled message type: " + msg.getClass().getSimpleName());
		}
	}

	public void sendToAllClients(Object message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
				System.out.println("Sent message: " + message.getClass().getSimpleName());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static String updateFlowerNameInDetails(String details, String oldName, String newName) {
		if (details == null || details.isEmpty()) return details;
		String[] parts = details.split(",");
		for (int i = 0; i < parts.length; i++) {
			String entry = parts[i].trim();
			if (entry.isEmpty()) continue;
			int idx = entry.lastIndexOf(' ');
			if (idx > 0) {
				String candidateName = entry.substring(0, idx).trim();
				String quantityPart = entry.substring(idx + 1).trim();
				// Only replace if candidateName matches oldName (case sensitive)
				if (candidateName.equals(oldName)) {
					parts[i] = newName + " " + quantityPart;
				} else {
					parts[i] = candidateName + " " + quantityPart; // reconstruct for clean format
				}
			} else {
				// If there's no quantity, keep as is
				parts[i] = entry;
			}
		}
		return String.join(", ", parts);
	}

	public void sendToClientRefreshedList(ConnectionToClient client) {
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			List<Flower> flowerList = session.createQuery("FROM Flower", Flower.class).getResultList();
			Map<Flower, byte[]> flowerImageMap = new HashMap<>();

			for (Flower flower : flowerList) {
				String imageFileName = flower.getImageId(); // e.g., "rose_red.jpg"
				try (InputStream is = getClass().getClassLoader().getResourceAsStream("FlowerPicture/" + imageFileName)) {
					if (is == null) {
						System.err.println("Image not found for: " + imageFileName);
						continue;
					}

					byte[] imageBytes = is.readAllBytes();
					flowerImageMap.put(flower, imageBytes);

				} catch (IOException e) {
					System.err.println("Error reading image for flower: " + flower.getName());
					e.printStackTrace();
				}
			}

			client.sendToClient(flowerImageMap);

		} catch (Exception e) {
			System.err.println("Failed to send refreshed flower list");
			e.printStackTrace();
		}finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();

		configuration.addAnnotatedClass(Account.class);
		configuration.addAnnotatedClass(Flower.class);
		configuration.addAnnotatedClass(PasswordResetCode.class);
		configuration.addAnnotatedClass(PasswordHistory.class);
		configuration.addAnnotatedClass(FeedBackSQL.class);
		configuration.addAnnotatedClass(OrderSQL.class);
		configuration.addAnnotatedClass(Branch.class);

		ServiceRegistry serviceRegistry = (new StandardServiceRegistryBuilder()).applySettings(configuration.getProperties()).build();
		return configuration.buildSessionFactory(serviceRegistry);
	}

	private String generate6DigitCode() {
		Random rand = new Random();
		int code = 100000 + rand.nextInt(900000);
		return String.valueOf(code);
	}

	private Date getExpiryTimestamp() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 10); // Code expires in 10 minutes
		return cal.getTime();
	}

	public static void checkAllAccountsAtMidnight() {
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();

			LocalDate today = LocalDate.now();
			List<Account> expiredAccounts = session.createQuery("FROM Account WHERE Subscription_expires_at <= :today", Account.class)
					.setParameter("today", java.sql.Date.valueOf(today))
					.getResultList();

			for (Account account : expiredAccounts) {
				account.setSubscribtion_level("Free");
				account.setAuto_renew_subscription(null);
				session.update(account);
			}

			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SubscribedClient getSubscribedClientByConnection(ConnectionToClient client) {
		for (SubscribedClient sub : SubscribersList) {
			if (sub.getClient().equals(client)) {
				return sub;
			}
		}
		return null;
	}

	public static void expireAllSubscriptionsAndNotifyLoggedClients() {
		try (Session session = sessionFactory.openSession()) {
			Transaction tx = session.beginTransaction();

			LocalDate today = LocalDate.now();
			java.sql.Date todaySql = java.sql.Date.valueOf(today);

			List<Account> toExpire = session.createQuery(
							"FROM Account WHERE Subscription_expires_at IS NOT NULL AND Subscription_expires_at <= :today", Account.class)
					.setParameter("today", todaySql)
					.getResultList();

			for (Account account : toExpire) {
				if ("Yes".equalsIgnoreCase(account.getAuto_renew_subscription())) {
					LocalDate newExpiry = LocalDate.now().plusYears(1);
					account.setSubscription_expires_at(newExpiry.toString());
				} else {
					account.setSubscription_expires_at(null);
					account.setAuto_renew_subscription(null);
					account.setSubscribtion_level("Free");
				}
				session.update(account);
			}

			tx.commit();

			System.out.println("Expired " + toExpire.size() + " subscriptions at " + LocalTime.now());

			for (SubscribedClient sub : SubscribersList) {
				Account acc = sub.getAccount();


				if (acc != null && acc.isLogged()) {
					System.out.println(acc.getEmail());
					try (Session session2 = sessionFactory.openSession()) {
						Account fresh = session2.get(Account.class, acc.getId());
						if (fresh != null) {
							sub.setAccount(fresh);
							sub.getClient().sendToClient(new AccountUpdateNotification(fresh));
						}
					} catch (IOException e) {
						System.err.println("Failed to notify client " + acc.getEmail());
						e.printStackTrace();
					}
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startOrderDeliverySweeper() {
		// run immediately, then every 60 seconds
		orderDeliverySweeper.scheduleAtFixedRate(this::checkAndMarkDelivered, 0, 60, TimeUnit.SECONDS);
	}

	private void stopOrderDeliverySweeper() {
		orderDeliverySweeper.shutdown();
		try {
			if (!orderDeliverySweeper.awaitTermination(5, TimeUnit.SECONDS)) {
				orderDeliverySweeper.shutdownNow();
			}
		} catch (InterruptedException ie) {
			orderDeliverySweeper.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private void checkAndMarkDelivered() {
		LocalDate today = LocalDate.now(sweeperZone);
		LocalTime nowTime = LocalTime.now(sweeperZone);

		// ---- Step 1: bulk update all strictly past dates ----
		try (Session s = sessionFactory.openSession()) {
			Transaction tx = s.beginTransaction();
			try {
				Query<?> q = s.createQuery(
						"update OrderSQL o " +
								"set o.status = :delivered " +
								"where (o.status is null or lower(o.status) <> 'delivered') " +
								"and o.deliveryDate < :today"
				);
				q.setParameter("delivered", "delivered");
				q.setParameter("today", java.sql.Date.valueOf(today));
				q.executeUpdate();
				tx.commit();
			} catch (Exception e) {
				tx.rollback();
				e.printStackTrace();
			}
		}

		// ---- Step 2: handle today's rows by time ----
		try (Session s = sessionFactory.openSession()) {
			Transaction tx = s.beginTransaction();
			try {
				List<OrderSQL> todays = s.createQuery(
								"from OrderSQL o " +
										"where (o.status is null or lower(o.status) <> 'delivered') " +
										"and o.deliveryDate = :today", OrderSQL.class)
						.setParameter("today", java.sql.Date.valueOf(today))
						.list();

				DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");

				for (OrderSQL o : todays) {
					String t = o.getDeliveryTime();
					LocalTime dueTime;

					if (t == null || t.isBlank()) {
						// treat missing as 00:00
						dueTime = LocalTime.MIDNIGHT;
					} else {
						try {
							dueTime = LocalTime.parse(t.trim(), hhmm);
						} catch (DateTimeParseException dtpe) {
							// bad format; skip but keep the server healthy
							System.err.println("[Sweeper] Bad delivery_time for order " + o.getId() + ": " + t);
							continue;
						}
					}

					if (!nowTime.isBefore(dueTime)) {
						o.setStatus("delivered");
						s.update(o);
					}
				}

				tx.commit();
			} catch (Exception e) {
				tx.rollback();
				e.printStackTrace();
			}
		}
	}

	public static void initializeSession() {
		try {
			sessionFactory = getSessionFactory();
			System.out.println("Hibernate session factory initialized.");

			// Reset all Account.logged to false at server startup
			try (Session session = sessionFactory.openSession()) {
				Transaction tx = session.beginTransaction();
				int updated = session.createQuery("UPDATE Account SET logged = false").executeUpdate();
				tx.commit();
				System.out.println("Reset logged status for " + updated + " accounts.");
			} catch (Exception e) {
				System.err.println("Failed to reset account logins:");
				e.printStackTrace();
			}

		} catch (Exception exception) {
			System.err.println("Failed to initialize Hibernate:");
			exception.printStackTrace();
		}
	}
}
