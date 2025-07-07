package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class SubscriptionRequest implements Serializable {
    private Account account;
    public SubscriptionRequest(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
