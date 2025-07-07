package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AutoRenewDisableResponse implements Serializable {
    private Account account;
    public AutoRenewDisableResponse(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
