package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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


public class SimpleServer extends AbstractServer {
	private static final List<SubscribedClient> SubscribersList = new CopyOnWriteArrayList<>();
	private static SessionFactory sessionFactory;
	private static Session session;

	public SimpleServer(int port) {
		super(port);
		initializeSession();

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
		else if (msg instanceof UpdateFlowerRequest updateRequest) {
			System.out.println("Received UpdateFlowerRequest from client");

			Flower updatedData = updateRequest.getFlower();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				Flower flowerInDb = session.get(Flower.class, updatedData.getId());

				if (flowerInDb != null) {
					flowerInDb.setName(updatedData.getName());
					flowerInDb.setPrice(updatedData.getPrice());
					flowerInDb.setDescription(updatedData.getDescription());
					flowerInDb.setColor(updatedData.getColor());
					flowerInDb.setSupply(updatedData.getSupply());

					session.update(flowerInDb);

					tx.commit();
					System.out.println("Updated flower in database: " + flowerInDb.getName());

					// Notify all clients (except maybe the manager who sent the update, if you want)
					UpdateFlowerNotification notification = new UpdateFlowerNotification(flowerInDb);
					for (SubscribedClient sub : SubscribersList) {
						try {
							sub.getClient().sendToClient(notification);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else {
					System.err.println("Flower with ID " + updatedData.getId() + " not found in DB.");
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
					feedback.setResolvedAt(java.time.LocalDateTime.now());
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
		
		else if (msg instanceof Order orderMsg) {
			System.out.println("Received Order from client!");

		else if (msg instanceof SubscriptionRequest) {
			SubscriptionRequest req = (SubscriptionRequest) msg;
			int accountId = req.getAccount().getId();

			Session session = null;
			Transaction tx = null;

			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();

				// Fetch managed customer object from DB
				Account customerInDb = session.get(Account.class, orderMsg.getCustomer().getId());
				orderMsg.setCustomer(customerInDb);

				// Save the order
				session.save(orderMsg);

				tx.commit();
				System.out.println("Order saved to database!");

			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();

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

		else if (msg instanceof UpdateFlowerSupplyRequest supplyRequest) {
			System.out.println("Received UpdateFlowerSupplyRequest from client");

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

				for (Flower updatedFlower : supplyRequest.getUpdatedFlowers()) {
					Flower flowerInDb = session.get(Flower.class, updatedFlower.getId());

					if (flowerInDb != null) {
						flowerInDb.setSupply(updatedFlower.getSupply());
						session.update(flowerInDb);
						System.out.println("Updated supply for: " + flowerInDb.getName() + " to " + flowerInDb.getSupply());
					} else {
						System.err.println("Flower with ID " + updatedFlower.getId() + " not found.");
					}
				}

				tx.commit();
				System.out.println("Flower supplies updated in database.");

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

				// Check if this password has ever been used for this account
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

				// Set previous current password entries to not current
				Query<PasswordHistory> currQ = session.createQuery(
						"FROM PasswordHistory WHERE user = :user AND isCurrent = true", PasswordHistory.class
				);
				currQ.setParameter("user", account);
				for (PasswordHistory ph : currQ.getResultList()) {
					ph.setCurrent(false);
					session.update(ph);
				}

				// Set new password for Account and save new PasswordHistory
				Account managedAccount = session.get(Account.class, account.getId());
				managedAccount.setPassword(newPassword);

				PasswordHistory newHist = new PasswordHistory(
						managedAccount, newPassword, true, new Date()
				);
				session.save(newHist);

				session.update(managedAccount);
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

		else {
			System.out.println("Unhandled message type: " + msg.getClass().getSimpleName());
		}
	}

	public void sendToAllClients(Object message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

			// Example: find all expired accounts (subscription_expires_at <= today)
			LocalDate today = LocalDate.now();
			List<Account> expiredAccounts = session.createQuery(
							"FROM Account WHERE subscription_expires_at <= :today", Account.class)
					.setParameter("today", java.sql.Date.valueOf(today))
					.getResultList();

			// Do whatever you need with these accounts
			for (Account account : expiredAccounts) {
				account.setSubscribtion_level("Free");
				account.setAuto_renew_subscription(null);
				session.update(account);
				// Optionally: Send email, etc.
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
							"FROM Account WHERE subscription_expires_at IS NOT NULL AND subscription_expires_at <= :today", Account.class)
					.setParameter("today", todaySql)
					.getResultList();

			for (Account account : toExpire) {
				account.setSubscription_expires_at(null);
				account.setAuto_renew_subscription(null);
				account.setSubscribtion_level("Free");
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
