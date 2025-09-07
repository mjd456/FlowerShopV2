package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import java.io.IOException;
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

public class SimpleServer extends AbstractServer {
    private static final List<SubscribedClient> SubscribersList = new CopyOnWriteArrayList<>();
    private static SessionFactory sessionFactory;
    private static Session session;
    private final ScheduledExecutorService orderDeliverySweeper = Executors.newSingleThreadScheduledExecutor();
    private final ZoneId sweeperZone = ZoneId.of("Asia/Jerusalem");

    private static final int DELIVERY_BRANCH_ID = 4;

    private static String branchName(int id) {
        return switch (id) {
            case 1 -> "Haifa";
            case 2 -> "Eilat";
            case 3 -> "Tel Aviv";
            default -> "Branch " + id;
        };
    }

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
            } else if (msgString.startsWith("GetAccounts")) {
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
            } else if (msgString.startsWith("RequestFlowerCatalogForManager")) {
                sendToClientRefreshedList(client);
            } else if (msgString.startsWith("Send all Feedbacks")) {
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
                        client.setInfo("account", fresh);

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
                                "Hey " + signupDetails.getFirstName() + "," + "\nWe are happy to have you as a customer."
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

            il.cshaifasweng.OCSFMediatorExample.entities.Account acc =
                    (il.cshaifasweng.OCSFMediatorExample.entities.Account) client.getInfo("account");
            boolean isNetworkManager = acc != null &&
                    acc.getAccountLevel() != null &&
                    acc.getAccountLevel().replaceAll("\\s+", "").equalsIgnoreCase("networkmanager");

            Session session = null;
            Transaction tx = null;

            try {
                session = sessionFactory.openSession();
                tx = session.beginTransaction();

                Flower flowerInDb = session.get(Flower.class, updatedData.getId());
                if (flowerInDb != null) {
                    String oldName = flowerInDb.getName();
                    String newName = updatedData.getName();

                    flowerInDb.setName(newName);
                    flowerInDb.setPrice(updatedData.getPrice());
                    flowerInDb.setDescription(updatedData.getDescription());
                    flowerInDb.setColor(updatedData.getColor());
                    flowerInDb.setDiscount(updatedData.getDiscount());

                    int haifa = Math.max(0, updatedData.getSupplyHaifa());
                    int eilat = Math.max(0, updatedData.getSupplyEilat());
                    int telAviv = Math.max(0, updatedData.getSupplyTelAviv());
                    int storage = Math.max(0, updatedData.getStorage());
                    flowerInDb.setSupplyHaifa(haifa);
                    flowerInDb.setSupplyEilat(eilat);
                    flowerInDb.setSupplyTelAviv(telAviv);
                    flowerInDb.setStorage(storage);
                    flowerInDb.setSupply(haifa + eilat + telAviv + storage);

                    if (isNetworkManager) {
                        String oldFile = flowerInDb.getImageId();

                        if (updateRequest.isDeleteImage()) {
                            if (oldFile != null && !oldFile.isBlank()) {
                                deleteFlowerImageFile(oldFile); // best-effort
                            }
                            flowerInDb.setImageId(null);

                        } else if (updateRequest.getNewImageJpeg() != null &&
                                updateRequest.getNewImageJpeg().length > 0) {

                            String saved = saveFlowerJpeg(updateRequest.getNewImageJpeg(),
                                    updateRequest.getSuggestedFileName());
                            flowerInDb.setImageId(saved);
                            if (oldFile != null && !oldFile.isBlank() && !oldFile.equals(saved)) {
                                deleteFlowerImageFile(oldFile);
                            }
                        }
                    } else {
                        if (updateRequest.isDeleteImage() || (updateRequest.getNewImageJpeg() != null))
                            System.out.println("Ignoring image update from non-NetworkManager (accId="
                                    + (acc != null ? acc.getId() : null) + ")");
                    }

                    session.update(flowerInDb);
                    session.flush();
                    session.refresh(flowerInDb);
                    if (!java.util.Objects.equals(oldName, newName)) {
                        List<OrderSQL> affectedOrders = session.createQuery(
                                        "FROM OrderSQL WHERE details LIKE :oldName", OrderSQL.class)
                                .setParameter("oldName", "%" + oldName + "%")
                                .list();
                        for (OrderSQL order : affectedOrders) {
                            String details = order.getDetails();
                            String updated = updateFlowerNameInDetails(details, oldName, newName);
                            if (!updated.equals(details)) {
                                order.setDetails(updated);
                                session.update(order);
                            }
                        }
                    }

                    tx.commit();
                    sendToAllClients(new UpdateFlowerNotification(flowerInDb));
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

                Account accountInDb = session.get(Account.class, requestAccount.getId());

                if (accountInDb != null) {
                    accountInDb.setLogged(false);
                    try {
                        client.sendToClient("Logout successful");
                    } catch (IOException e) {
                        System.err.println("Client disconnected during send.");
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

                tx.commit();
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

            try {
                tx = session.beginTransaction();

                int accountId = feedbackMsg.getAccount().getId();
                Account account = session.get(Account.class, accountId);
                if (account == null) {
                    client.sendToClient("Error: Account not found in database.");
                    tx.rollback();
                    return;
                }

                Branch branchEntity = null;
                String branchName = feedbackMsg.getBranch(); // String from client
                if (branchName != null && !branchName.isBlank()) {
                    String normalized = branchName.trim().toLowerCase();
                    if (normalized.equals("tel aviv") || normalized.equals("tel-aviv")) {
                        normalized = "telaviv";
                    }

                    // Find by name case-insensitively
                    branchEntity = session.createQuery(
                                    "from Branch b where lower(b.name) = :name", Branch.class)
                            .setParameter("name", normalized)
                            .setMaxResults(1)
                            .uniqueResult();
                }

                FeedBackSQL feedbackEntity = new FeedBackSQL(
                        account,
                        feedbackMsg.getfeedbackTtitle(),
                        feedbackMsg.getfeedbackTdesc(),
                        branchEntity
                );

                session.save(feedbackEntity);

                feedbackEntity.getAccount().getEmail();

                tx.commit();
                System.out.println("Feedback succeeded for account: " + accountId);
                client.sendToClient("Feedback added successfully");

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
                feedbacks = session.createQuery(
                                "FROM FeedBackSQL WHERE account.id = :id ORDER BY submittedAt DESC", FeedBackSQL.class)
                        .setParameter("id", accountId)
                        .getResultList();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.close();
            }

            try {
                client.sendToClient(new GetUserFeedbacksResponse(feedbacks));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof QuarterlyRevenueReportRequest req) {
            System.out.println("QuarterlyRevenueReportRequest: branchId=" + req.getBranchId()
                    + ", from=" + req.getFrom() + ", to=" + req.getTo());

            try (Session s = sessionFactory.openSession()) {

                String hql =
                        "SELECT o.pickupBranch, SUM(COALESCE(o.totalPrice, 0)) " +
                                "FROM   OrderSQL o " +
                                "WHERE  LOWER(o.status) = 'delivered' " +
                                "  AND  o.deliveryDate BETWEEN :from AND :to " +
                                (req.getBranchId() > 0 ? "  AND  o.pickupBranch = :branchId " : "") +
                                "GROUP BY o.pickupBranch " +
                                "ORDER BY o.pickupBranch";

                Query<Object[]> q = s.createQuery(hql, Object[].class)
                        .setParameter("from", req.getFrom())
                        .setParameter("to",   req.getTo());

                if (req.getBranchId() > 0) {
                    q.setParameter("branchId", req.getBranchId());
                }

                List<Object[]> rows = q.list();

                Map<Integer, Double> pickupByBranch = new LinkedHashMap<>();
                double deliveryTotal = 0.0;

                for (Object[] r : rows) {
                    Integer pb = (r[0] == null) ? null : ((Number) r[0]).intValue();
                    double sum = (r[1] != null) ? ((Number) r[1]).doubleValue() : 0.0;

                    boolean isDelivery = (pb == null) || pb.equals(DELIVERY_BRANCH_ID);

                    if (req.getBranchId() == 0) {
                        if (isDelivery) {
                            deliveryTotal += sum;
                        } else {
                            pickupByBranch.merge(pb, sum, Double::sum);
                        }
                    } else {
                        if (!isDelivery && pb != null && pb == req.getBranchId()) {
                            pickupByBranch.merge(pb, sum, Double::sum);
                        }
                    }
                }

                List<QuarterlyRevenueReportResponse.BreakdownRow> breakdown = new ArrayList<>();

                if (req.getBranchId() == 0) {
                    for (var e : pickupByBranch.entrySet()) {
                        int bid = e.getKey();
                        breakdown.add(new QuarterlyRevenueReportResponse.BreakdownRow(
                                bid, branchName(bid), "PICKUP", e.getValue()
                        ));
                    }
                    breakdown.add(new QuarterlyRevenueReportResponse.BreakdownRow(
                            null, "Delivery", "DELIVERY", deliveryTotal
                    ));
                } else {
                    int bid = req.getBranchId();
                    double v = pickupByBranch.getOrDefault(bid, 0.0);
                    breakdown.add(new QuarterlyRevenueReportResponse.BreakdownRow(
                            bid, branchName(bid), "PICKUP", v
                    ));
                }

                QuarterlyRevenueReportResponse resp = new QuarterlyRevenueReportResponse(new ArrayList<>());
                resp.setBreakdown(breakdown);
                client.sendToClient(resp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof OrdersByProductTypeReportRequest req) {
            System.out.println("OrdersByProductTypeReportRequest: branchId=" + req.getBranchId()
                    + ", from=" + req.getFrom() + ", to=" + req.getTo());

            try (Session s = sessionFactory.openSession()) {

                String hql =
                        "FROM OrderSQL o " +
                                "WHERE LOWER(o.status) IN ('delivered','upcoming') " +
                                "  AND o.deliveryDate BETWEEN :from AND :toPlus " +
                                (req.getBranchId() > 0 ? "AND o.pickupBranch = :branchId " : "") +
                                "ORDER BY o.deliveryDate ASC";

                java.util.Date toPlus = new java.util.Date(
                        req.getTo().getTime() + java.util.concurrent.TimeUnit.DAYS.toMillis(365)
                );

                Query<OrderSQL> q = s.createQuery(hql, OrderSQL.class)
                        .setParameter("from", req.getFrom())
                        .setParameter("toPlus", toPlus);

                if (req.getBranchId() > 0) q.setParameter("branchId", req.getBranchId());

                java.util.List<OrderSQL> orders = q.list();

                java.util.Map<String, Long> qtyByName = new java.util.LinkedHashMap<>();
                java.util.Map<String, Long> ordersByName = new java.util.LinkedHashMap<>();

                for (OrderSQL o : orders) {
                    String details = o.getDetails();
                    if (details == null || details.isBlank()) continue;

                    java.util.Map<String, Integer> items = parseDetailsNameQty(details);
                    if (items.isEmpty()) continue;

                    java.util.Set<String> seenInThisOrder = new java.util.HashSet<>();

                    for (var e : items.entrySet()) {
                        String name = e.getKey();
                        int qty = e.getValue();
                        if (qty <= 0) continue;

                        qtyByName.merge(name, (long) qty, Long::sum);

                        if (seenInThisOrder.add(name)) {
                            ordersByName.merge(name, 1L, Long::sum);
                        }
                    }
                }

                java.util.List<OrdersByProductTypeReportResponse.Row> rows = new java.util.ArrayList<>();
                java.util.List<java.util.Map.Entry<String, Long>> sorted =
                        new java.util.ArrayList<>(qtyByName.entrySet());
                sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

                for (var e : sorted) {
                    String product = e.getKey();
                    long quantity  = e.getValue();
                    long ordersCnt = ordersByName.getOrDefault(product, 0L);
                    double total   = 0.0; // we can't compute revenue per flower from 'details' (names only)
                    rows.add(new OrdersByProductTypeReportResponse.Row(product, ordersCnt, quantity, total));
                }

                client.sendToClient(new OrdersByProductTypeReportResponse(rows));
            } catch (Exception ex) {
                ex.printStackTrace();
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

                FeedBackSQL feedback = session.get(FeedBackSQL.class, feedbackId);
                if (feedback != null) {
                    feedback.setStatus(newStatus);
                    feedback.setResolvedAt(LocalDateTime.now());
                    session.update(feedback);
                    tx.commit();

                    List<FeedBackSQL> allFeedbacks = session.createQuery("FROM FeedBackSQL", FeedBackSQL.class).getResultList();
                    client.sendToClient(new GetAllFeedbacksResponse(allFeedbacks));
                } else {
                    client.sendToClient("Error: Feedback not found.");
                    if (tx != null) tx.rollback();
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                try {
                    client.sendToClient("Error updating feedback: " + e.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                try {
                    client.sendToClient("Error upgrading subscription.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

                if ("Yes".equalsIgnoreCase(account.getAuto_renew_subscription())) {
                    account.setAuto_renew_subscription("No");
                    session.update(account);
                    tx.commit();
                    client.sendToClient(new AutoRenewDisableResponse(account));
                } else {
                    tx.commit();
                    client.sendToClient("Auto-renew was already disabled.");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                try {
                    client.sendToClient("Error canceling auto-renew.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                    // capture before entity is gone
                    String imageFile = flower.getImageId();

                    session.delete(flower);
                    tx.commit();
                    System.out.println("Flower deleted: " + flower.getName());

                    if (imageFile != null && !imageFile.isBlank()) {
                        boolean deleted = deleteFlowerImageFile(imageFile);
                        if (!deleted) {
                            System.err.println("[images] could not delete: " + imageFile);
                        }
                    }

                    sendToAllClients(new FlowerDeleted(idToDelete));
                } else {
                    tx.rollback();
                    System.out.println("Flower to delete not found");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (msg instanceof AddFlowerRequest addFlowerRequest) {
            System.out.println("Received AddFlowerRequest from client.");

            Flower newFlower = addFlowerRequest.getFlower();

            byte[] jpeg = addFlowerRequest.getImageJpeg();
            String suggested = addFlowerRequest.getSuggestedFileName();

            Session session = null;
            Transaction tx = null;

            try {

                if (jpeg != null && jpeg.length > 0) {
                    String savedName = saveFlowerJpeg(jpeg, suggested);
                    newFlower.setImageId(savedName);
                } else {
                    newFlower.setImageId(null);
                }

                int total = Math.max(0, newFlower.getSupplyHaifa())
                        + Math.max(0, newFlower.getSupplyEilat())
                        + Math.max(0, newFlower.getSupplyTelAviv())
                        + Math.max(0, newFlower.getStorage());
                newFlower.setSupply(total);

                session = sessionFactory.openSession();
                tx = session.beginTransaction();

                session.save(newFlower);


                tx.commit();
                System.out.println("New flower added to DB: " + newFlower.getName());

                client.sendToClient("Flower added successfully");


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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } finally {
                if (session != null) session.close();
            }
        }
        else if (msg instanceof UpdatePasswordRequest req) {
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

                Query<PasswordHistory> currQ = session.createQuery(
                        "FROM PasswordHistory WHERE user = :user AND isCurrent = true", PasswordHistory.class
                );
                currQ.setParameter("user", account);
                for (PasswordHistory ph : currQ.getResultList()) {
                    ph.setCurrent(false);
                    ph.setSwappedAt(Calendar.getInstance().getTime());
                    session.update(ph);
                }

                Account managedAccount = session.get(Account.class, account.getId());
                managedAccount.setPassword(newPassword);
                session.update(managedAccount);

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

                client.sendToClient(new UpdateCreditCardResponse(true, "Credit card updated.", account));
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
                    client.sendToClient(new UpdateCreditCardResponse(false, "Failed to update credit card info.", null));
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

                String details = order.getDetails();
                Integer branchId = null;
                if (order.getPickupBranch() != null && order.getPickupBranch() != null) {
                    branchId = order.getPickupBranch();
                }

                if (details != null && !details.isBlank()) {
                    String[] parts = details.split(",");
                    for (String part : parts) {
                        part = part.trim();
                        if (part.isEmpty()) continue;

                        int xIdx = part.lastIndexOf('x');
                        if (xIdx <= 0) continue;

                        String name = part.substring(0, xIdx).trim();
                        String qtyStr = part.substring(xIdx + 1).replaceAll("[^\\d]", "").trim();
                        int qty = qtyStr.isEmpty() ? 1 : Integer.parseInt(qtyStr);
                        if (qty <= 0) continue;

                        // Find the flower by exact name
                        Flower flower = session.createQuery("from Flower where name = :name", Flower.class)
                                .setParameter("name", name)
                                .uniqueResult();

                        if (flower == null) continue;

                        // Restock by branch or storage
                        if (branchId != null && branchId > 0) {
                            switch (branchId) {
                                case 1: // Haifa
                                    flower.setSupplyHaifa(flower.getSupplyHaifa() + qty);
                                    break;
                                case 2: // Eilat
                                    flower.setSupplyEilat(flower.getSupplyEilat() + qty);
                                    break;
                                case 3: // TelAviv
                                    flower.setSupplyTelAviv(flower.getSupplyTelAviv() + qty);
                                    break;
                                default:
                                    flower.setStorage(flower.getStorage() + qty);
                                    break;
                            }
                        } else {
                            // Delivery: goes back to Storage
                            flower.setStorage(flower.getStorage() + qty);
                        }

                        // keep total supply consistent
                        int total = flower.getSupplyHaifa() + flower.getSupplyEilat() + flower.getSupplyTelAviv() + flower.getStorage();
                        flower.setSupply(total);

                        session.update(flower);
                        restockedFlowers.add(flower);
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

                for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
                    Flower clientFlower = entry.getKey();
                    int qty = entry.getValue();

                    if (clientFlower.getId() <= 0) continue;

                    Flower flower = session.get(Flower.class, clientFlower.getId());
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

                for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
                    Flower clientFlower = entry.getKey();
                    int qty = entry.getValue();
                    if (clientFlower.getId() <= 0) continue;

                    Flower flower = session.get(Flower.class, clientFlower.getId());
                    flower.setSupply(flower.getSupply() - qty);
                    flower.setPopularity(flower.getPopularity() + 1);
                    session.update(flower);
                }

                Account managedAccount = session.get(Account.class, request.getCustomer().getId());
                if (managedAccount == null) {
                    tx.rollback();
                    client.sendToClient(new PlaceOrderResponse(false, "Account not found!"));
                    return;
                }
                System.out.println("=== CartMap after sending ===");
                for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
                    System.out.println("Flower: " + entry.getKey().getName()
                            + " | Qty: " + entry.getValue()
                            + " | Price: " + entry.getKey().getPrice()
                            + " | Discount: " + entry.getKey().getDiscount());
                }
                System.out.println("================================");
                StringBuilder details = new StringBuilder();
                for (Map.Entry<Flower, Integer> entry : request.getCartMap().entrySet()) {
                    details.append(entry.getKey().getName())
                            .append(" x")
                            .append(entry.getValue())
                            .append(", ");
                }
                if (details.length() > 2) details.setLength(details.length() - 2);

                Integer branchId = request.getPickupBranchId();

                OrderSQL orderSQL = new OrderSQL(
                        managedAccount,
                        java.sql.Date.valueOf(request.getDate()),
                        request.getTime(),
                        request.getStatus(),
                        details.toString(),
                        request.getTotalPrice(),
                        request.getAddressOrPickup(),
                        request.getGreeting(),
                        branchId
                );
                orderSQL.setAccount(managedAccount);

                session.save(orderSQL);
                tx.commit();

                client.sendToClient(new PlaceOrderResponse(true, "Order placed and saved successfully!"));
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
        else if (msg instanceof Account) {
            Account account = (Account) msg;
            Session session = null;
            Transaction tx = null;
            List<Account> accounts = new ArrayList<>();
            try {
                session = sessionFactory.openSession();
                tx = session.beginTransaction();

                Account oldAccount = session.get(Account.class, account.getId());
                Branch newBranch = session.get(Branch.class, account.getBranch().getId());
                if (oldAccount != null) {
                    oldAccount.setFirstName(account.getFirstName());
                    oldAccount.setLastName(account.getLastName());
                    oldAccount.setEmail(account.getEmail());
                    oldAccount.setIdentityNumber(account.getIdentityNumber());
                    oldAccount.setPassword(account.getPassword());
                    oldAccount.setCreditCardValidUntil(account.getCreditCardValidUntil());
                    oldAccount.setCreditCardNumber(account.getCreditCardNumber());
                    oldAccount.setPhoneNumber(account.getPhoneNumber());
                    oldAccount.setAccountLevel(account.getAccountLevel());
                    oldAccount.setBranch(newBranch);
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
        }else if (msg instanceof il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramRequest req) {
            try (Session s = sessionFactory.openSession()) {
                java.time.LocalDateTime fromDT = req.getFrom().toLocalDate().atStartOfDay();
                java.time.LocalDateTime toDT   = req.getTo().toLocalDate().atTime(23, 59, 59);

                java.util.List<FeedBackSQL> all = s.createQuery(
                                "from FeedBackSQL f where f.submittedAt >= :from and f.submittedAt <= :to",
                                FeedBackSQL.class
                        ).setParameter("from", fromDT)
                        .setParameter("to",   toDT)
                        .list();

                class Agg { int id; String name; long pending, resolved, rejected; }
                java.util.Map<Integer, Agg> byBranch = new java.util.LinkedHashMap<>();

                for (FeedBackSQL f : all) {
                    if (f.getSubmittedAt() == null) continue;

                    int bid = 0; String bname = "Network";
                    if (f.getBranch() != null) {
                        bid = f.getBranch().getId();
                        if (f.getBranch().getName() != null) bname = f.getBranch().getName();
                    } else if (f.getAccount() != null && f.getAccount().getBranch() != null) {
                        bid = f.getAccount().getBranch().getId();
                        if (f.getAccount().getBranch().getName() != null) bname = f.getAccount().getBranch().getName();
                    }

                    Agg a = byBranch.get(bid);
                    if (a == null) {
                        a = new Agg();
                        a.id = bid;
                        a.name = bname;
                        byBranch.put(bid, a);
                    }

                    FeedBackSQL.FeedbackStatus st = f.getStatus();
                    if (st == null) {
                        a.pending++;
                    } else {
                        switch (st) {
                            case Pending  -> a.pending++;
                            case Resolved -> a.resolved++;
                            case Rejected -> a.rejected++;
                            default       -> a.pending++;
                        }
                    }
                }

                java.util.List<il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramResponse.Row> rows =
                        new java.util.ArrayList<>();
                for (Agg a : byBranch.values()) {
                    rows.add(new il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramResponse.Row(
                            a.id, a.name, a.pending, a.resolved, a.rejected
                    ));
                }

                rows.sort(java.util.Comparator
                        .comparingLong(il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramResponse.Row::getAttention)
                        .reversed()
                        .thenComparing(il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramResponse.Row::getBranchName,
                                String.CASE_INSENSITIVE_ORDER));

                client.sendToClient(new il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsByBranchHistogramResponse(rows));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        else if (msg instanceof CompareReportsRequest req) {

            il.cshaifasweng.OCSFMediatorExample.entities.Account acc =
                    (il.cshaifasweng.OCSFMediatorExample.entities.Account) client.getInfo("account");

            int effectiveBranchId;
            if (acc != null && acc.getAccountLevel() != null
                    && acc.getAccountLevel().trim().equalsIgnoreCase("BranchManager")) {
                effectiveBranchId = (acc.getBranch() != null) ? acc.getBranch().getId() : 0;
            } else {
                effectiveBranchId = req.getBranchId();
            }

            ReportMetrics A = computeMetrics(req.getReportType(), effectiveBranchId, req.getDateA());
            ReportMetrics B = computeMetrics(req.getReportType(), effectiveBranchId, req.getDateB());

            String branchName = branchNameFor(effectiveBranchId);
            il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse resp =
                    new il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse(
                            req.getDateA(), req.getDateB(), branchName, req.getReportType()
                    );

            resp.addRow("Total Orders", A.totalOrders, B.totalOrders);
            resp.addRow("Revenue (₪)", A.revenue, B.revenue);
            resp.addRow("Avg Order Value (₪)", A.avgOrderValue, B.avgOrderValue);
            resp.addRow("Complaints", A.complaints, B.complaints);

            try {
                client.sendToClient(resp);
            } catch (java.io.IOException e) {
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
                if (candidateName.equals(oldName)) {
                    parts[i] = newName + " " + quantityPart;
                } else {
                    parts[i] = candidateName + " " + quantityPart;
                }
            } else {
                parts[i] = entry;
            }
        }
        return String.join(", ", parts);
    }

    public void sendToClientRefreshedList(ConnectionToClient client) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            List<Flower> flowerList = session.createQuery("FROM Flower", Flower.class).getResultList();
            Map<Flower, byte[]> flowerImageMap = new HashMap<>();

            for (Flower flower : flowerList) {
                String imageFileName = flower.getImageId();
                byte[] imageBytes = null;

                if (imageFileName != null && !imageFileName.isBlank()) {
                    try {
                        imageBytes = readFlowerImage(imageFileName);
                    } catch (IOException nf) {
                        System.err.println("Image not found for: " + imageFileName);
                    }

                }

                flowerImageMap.put(flower, imageBytes);
            }

            client.sendToClient(flowerImageMap);
        } catch (Exception e) {
            System.err.println("Failed to send refreshed flower list");
            e.printStackTrace();
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

    static class ReportMetrics {
        int totalOrders;
        double revenue;
        double avgOrderValue;
        int complaints;
    }

    private String branchNameFor(int id) {
        switch (id) {
            case 0:
                return "Network";
            case 1:
                return "Haifa";
            case 2:
                return "Eilat";
            case 3:
                return "Tel Aviv";
            default:
                return "Branch " + id;
        }
    }

    private byte[] readFlowerImage(String fileName) throws IOException {
        java.nio.file.Path ext = java.nio.file.Paths.get("data", "FlowerPicture", fileName);
        if (java.nio.file.Files.exists(ext)) return java.nio.file.Files.readAllBytes(ext);

        // dev sources folder (when running from module in IDE)
        java.nio.file.Path dev = java.nio.file.Paths.get("src", "main", "resources", "FlowerPicture", fileName);
        if (java.nio.file.Files.exists(dev)) return java.nio.file.Files.readAllBytes(dev);

        // packaged resource (built-in images)
        try (java.io.InputStream is =
                     getClass().getClassLoader().getResourceAsStream("FlowerPicture/" + fileName)) {
            if (is != null) return is.readAllBytes();
        }

        throw new java.io.FileNotFoundException(fileName);

    }

    private ReportMetrics computeMetrics(String reportType, int branchId, java.sql.Date date) {
        ReportMetrics m = new ReportMetrics();

        try (Session s = sessionFactory.openSession()) {
            String hqlOrders =
                    "select count(o), sum( coalesce(o.totalPrice,0) - coalesce(o.refundAmount,0) ) " +
                            "from OrderSQL o " +
                            "where lower(o.status) = 'delivered' " +
                            "  and o.deliveryDate = :day " +
                            (branchId > 0 ? "  and o.pickupBranch = :bid " : "");

            Query<Object[]> q1 = s.createQuery(hqlOrders, Object[].class)
                    .setParameter("day", date);
            if (branchId > 0) q1.setParameter("bid", branchId);

            Object[] row = q1.uniqueResult();
            long orders = (row == null || row[0] == null) ? 0L : ((Number) row[0]).longValue();
            double revenue = (row == null || row[1] == null) ? 0.0 : ((Number) row[1]).doubleValue();

            m.totalOrders = (int) orders;
            m.revenue = revenue;
            m.avgOrderValue = orders == 0 ? 0.0 : Math.round((revenue / orders) * 100.0) / 100.0;

            java.time.LocalDate d = date.toLocalDate();
            java.time.LocalDateTime start = d.atStartOfDay();
            java.time.LocalDateTime end = start.plusDays(1);

            String hqlComplaints =
                    "select count(f) " +
                            "from FeedBackSQL f " +
                            "where f.submittedAt >= :start and f.submittedAt < :end " +
                            (branchId > 0 ? "  and f.account.branch.id = :bid " : "");

            Query<Long> q2 = s.createQuery(hqlComplaints, Long.class)
                    .setParameter("start", start)
                    .setParameter("end", end);
            if (branchId > 0) q2.setParameter("bid", branchId);

            Long complaints = q2.uniqueResult();
            m.complaints = (complaints == null) ? 0 : complaints.intValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    private String saveFlowerJpeg(byte[] data, String suggestedFileName) throws IOException {
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data)) {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(bais);
            if (img == null) throw new IOException("Invalid image data");
        }

        java.nio.file.Path dir = resolveFlowerDir();

        String base = (suggestedFileName == null || suggestedFileName.isBlank())
                ? "image.jpg"
                : sanitizeBaseName(suggestedFileName);

        if (base.toLowerCase().endsWith(".jpeg")) base = base.substring(0, base.length() - 5) + ".jpg";
        if (!base.toLowerCase().endsWith(".jpg")) base = base + ".jpg";

        String unique = uniqueName(dir, base);
        java.nio.file.Path out = dir.resolve(unique);
        java.nio.file.Files.write(out, data, java.nio.file.StandardOpenOption.CREATE_NEW);

        System.out.println("[images] saved: " + out.toAbsolutePath());
        return unique; // filename to store in Flower.imageId
    }

    private boolean deleteFlowerImageFile(String fileName) {
        boolean ok = false;
        // external/prod
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("data", "FlowerPicture", fileName);
            if (java.nio.file.Files.deleteIfExists(p)) {
                System.out.println("[images] deleted (external): " + p.toAbsolutePath());
                ok = true;
            }
        } catch (Exception ignore) {
        }

        // dev resources
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("src", "main", "resources", "FlowerPicture", fileName);
            if (java.nio.file.Files.deleteIfExists(p)) {
                System.out.println("[images] deleted (dev): " + p.toAbsolutePath());
                ok = true;
            }
        } catch (Exception ignore) {
        }

        return ok;
    }

    private java.nio.file.Path resolveFlowerDir() throws IOException {
        java.nio.file.Path dev = java.nio.file.Paths.get("src", "main", "resources", "FlowerPicture");
        if (java.nio.file.Files.exists(java.nio.file.Paths.get("src"))) {
            java.nio.file.Files.createDirectories(dev);
            return dev;
        }
        java.nio.file.Path ext = java.nio.file.Paths.get("data", "FlowerPicture");
        java.nio.file.Files.createDirectories(ext);
        return ext;
    }

    private String sanitizeBaseName(String name) {
        String base = name.replace("\\", "/");                       // strip any path
        base = base.substring(base.lastIndexOf('/') + 1);
        base = base.replaceAll("[^A-Za-z0-9._-]", "_");
        return base.isBlank() ? "image.jpg" : base;
    }

    private String uniqueName(java.nio.file.Path dir, String fileName) throws IOException {
        int dot = fileName.lastIndexOf('.');
        String stem = (dot >= 0 ? fileName.substring(0, dot) : fileName);
        String ext = (dot >= 0 ? fileName.substring(dot) : "");
        java.nio.file.Path candidate = dir.resolve(fileName);
        int i = 1;
        while (java.nio.file.Files.exists(candidate)) {
            candidate = dir.resolve(stem + "-" + i + ext);
            i++;
        }
        return candidate.getFileName().toString();
    }

    private static java.util.Date toUtilDate(Object t) {
        if (t == null) return null;
        if (t instanceof java.util.Date) {
            return (java.util.Date) t;                       // java.util.Date or java.sql.Date is fine
        }
        if (t instanceof java.time.LocalDate) {
            return java.sql.Date.valueOf((java.time.LocalDate) t);
        }
        if (t instanceof java.time.LocalDateTime) {
            return java.util.Date.from(((java.time.LocalDateTime) t)
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        throw new IllegalArgumentException("Unsupported date type: " + t.getClass());
    }
    /** Parse strings like "Rose x2, Tulip x1" into {Rose=2, Tulip=1}. Case- and space-tolerant. */
    private static java.util.Map<String, Integer> parseDetailsNameQty(String details) {
        java.util.Map<String, Integer> out = new java.util.LinkedHashMap<>();
        if (details == null) return out;

        // Split by comma
        String[] parts = details.split(",");
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(.+?)\\s*[xX]\\s*(\\d+)\\s*$");

        for (String raw : parts) {
            String part = raw.trim();
            if (part.isEmpty()) continue;

            java.util.regex.Matcher m = p.matcher(part);
            if (m.matches()) {
                String name = m.group(1).trim();
                int qty = Integer.parseInt(m.group(2));
                if (!name.isEmpty() && qty > 0) {
                    out.merge(name, qty, Integer::sum);
                }
            } else {
                // If no " xN", treat the whole token as a name with qty 1
                out.merge(part, 1, Integer::sum);
            }
        }
        return out;
    }

}
