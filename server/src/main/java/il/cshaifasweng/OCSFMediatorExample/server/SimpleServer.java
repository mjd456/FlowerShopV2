package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static SessionFactory sessionFactory;
	private static Session session;

	public SimpleServer(int port) {
		super(port);
		initializeSession();
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

		if (msg instanceof String msgString) {
			if (msgString.startsWith("#warning")) {
				Warning warning = new Warning("Warning from server!");
				try {
					client.sendToClient(warning);
					System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (msgString.startsWith("add client")) {
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				try {
					client.sendToClient("client added successfully");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (msgString.startsWith("remove client")) {
				if (!SubscribersList.isEmpty()) {
					for (SubscribedClient subscribedClient : SubscribersList) {
						if (subscribedClient.getClient().equals(client)) {
							SubscribersList.remove(subscribedClient);
							break;
						}
					}
				}
			} else if (msgString.startsWith("RefreshList")) {
				sendToClientRefreshedList(client);
			} else if (msgString.startsWith("Does this email exist?")) {
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
			} else if (msgString.startsWith("Is the code correct?")) {
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
			} else if (msgString.startsWith("Is this password unique?")) {
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
			} else if (msgString.startsWith("RequestFlowerCatalogForManager")) {
				sendToClientRefreshedList(client);
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
					// Account not found → deny login
					client.sendToClient(new LoginResponse(false, -1, "-", null));
				} else if (account.isLogged()) {
					// Already logged in → deny login
					client.sendToClient(new LoginResponse(false, -1, "-", null));
				} else {
					// Not logged in → mark as logged in and update DB
					try (Session session = sessionFactory.openSession()) {
						Transaction tx = session.beginTransaction();

						Account accountInDb = session.get(Account.class, account.getId()); // safer
						accountInDb.setLogged(true); // set the flag on the managed object

						tx.commit();

						// Send success response
						client.sendToClient(new LoginResponse(
								true,
								accountInDb.getId(),
								accountInDb.getFirstName(),
								accountInDb
						));
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

					session.update(flowerInDb);

					tx.commit();
					System.out.println("Updated flower in database: " + flowerInDb.getName());
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
			System.out.println("Logout request from client"); // ✅ Confirm this prints

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
						e.printStackTrace();
						SubscribersList.remove(client);
					}
				} else {
					System.err.println("Account not found in DB (LogoutRequest).");
				}

				tx.commit();
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
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

	public static void initializeSession() {
		try {
			sessionFactory = getSessionFactory();
			System.out.println("Hibernate session factory initialized.");
		} catch (Exception exception) {
			System.err.println("Failed to initialize Hibernate:");
			exception.printStackTrace();
		}
	}

}
