package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;

public class SetAccountLevel {
    Account account;
    public SetAccountLevel(Account accountLevel) {
        this.account = accountLevel;
    }

    public Account getAccount() {
        return account;
    }
}
