package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AccountUpdateNotification implements Serializable {
    private Account account;
    public AccountUpdateNotification(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
