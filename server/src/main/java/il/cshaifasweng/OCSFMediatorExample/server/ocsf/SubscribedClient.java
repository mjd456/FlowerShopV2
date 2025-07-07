package il.cshaifasweng.OCSFMediatorExample.server.ocsf;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;

public class SubscribedClient {
    private ConnectionToClient client;
    private Account account;

    public SubscribedClient(ConnectionToClient client) {
        this.client = client;
        this.account = null;
    }

    public SubscribedClient(ConnectionToClient client, Account account) {
        this.client = client;
        this.account = account;
    }

    public ConnectionToClient getClient() {
        return client;
    }

    public void setClient(ConnectionToClient client) {
        this.client = client;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}