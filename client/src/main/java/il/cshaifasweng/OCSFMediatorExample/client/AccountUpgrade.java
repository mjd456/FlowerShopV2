package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;

public class AccountUpgrade {
    private Account account;
    public AccountUpgrade(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
