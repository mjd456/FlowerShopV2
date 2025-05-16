package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
			}
		}
		else if (msg instanceof LoginRequest loginRequest) {
			String email = loginRequest.getUsername();
			String password = loginRequest.getPassword();

			System.out.println("Received login request from client:");
			System.out.println("email: " + email);
			System.out.println("Password: " + password);

			boolean success = false;
			Account account = null;


			try {
				session = sessionFactory.openSession();
				session.beginTransaction();

				Query<Account> query = session.createQuery("FROM Account WHERE email = :email AND password = :password", Account.class);
				query.setParameter("email", email);
				query.setParameter("password", password);

				List<Account> all = session.createQuery("FROM Account", Account.class).getResultList();
				for (Account a : all) {
					System.out.println(">> EMAIL in DB: '" + a.getEmail() + "'");
				}

				List<Account> results = query.getResultList();
				session.getTransaction().commit();

				if (!results.isEmpty()) {
					account = results.get(0);
					success = true;
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
			try {
				System.out.println(success);
				client.sendToClient(new LoginResponse(success, account != null ? account.getId() : -1,account != null ? account.getFirstName():"-" ,account));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof SignUpRequest signupDetails) {
			System.out.println("📥 Received SignUp request from client");

			Session session = null;

			try {
				session = sessionFactory.openSession();
				session.beginTransaction();

				// 1. Check if email already exists
				Query<Account> query = session.createQuery("FROM Account WHERE email = :email", Account.class);
				query.setParameter("email", signupDetails.getEmail());

				List<Account> existing = query.getResultList();

				if (!existing.isEmpty()) {
					System.out.println("❌ Email already exists: " + signupDetails.getEmail());
					client.sendToClient("Email already exists");
				} else {
					// 2. Create new Account object
					Account newAccount = new Account();
					newAccount.setEmail(signupDetails.getEmail());
					newAccount.setPassword(signupDetails.getPassword());
					newAccount.setFirstName(signupDetails.getFirstName());
					newAccount.setLastName(signupDetails.getLastName());
					newAccount.setIdentityNumber(signupDetails.getIdentityNumber());
					newAccount.setCreditCardNumber(signupDetails.getCreditCardNumber());
					newAccount.setCvv(signupDetails.getCvv());
					newAccount.setCreditCardValidUntil(signupDetails.getCreditCardValidUntil());

					// 3. Set account level
					newAccount.setAccountLevel("Customer");

					// 4. Save to DB
					session.save(newAccount);
					session.getTransaction().commit();

					System.out.println("✅ Sign-up succeeded for email: " + signupDetails.getEmail());
					client.sendToClient("Sign-up succeeded for email");
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

	public static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();

		configuration.addAnnotatedClass(Account.class);

		ServiceRegistry serviceRegistry = (new StandardServiceRegistryBuilder()).applySettings(configuration.getProperties()).build();
		return configuration.buildSessionFactory(serviceRegistry);
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
