package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;

public class AutoRenewResponse {
    private Account account;
    public AutoRenewResponse(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
