package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;

public class AccountUpdate {
    private Account account;
    public AccountUpdate(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
