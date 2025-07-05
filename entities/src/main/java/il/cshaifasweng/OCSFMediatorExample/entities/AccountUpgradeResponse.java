package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AccountUpgradeResponse implements Serializable {
    private Account account;
    public AccountUpgradeResponse(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
