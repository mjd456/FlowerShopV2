package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class CancelAutoRenewRequest implements Serializable {
    private Account account;

    public CancelAutoRenewRequest(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
