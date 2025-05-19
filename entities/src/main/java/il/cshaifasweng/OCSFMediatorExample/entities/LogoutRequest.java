package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class LogoutRequest implements Serializable {
    private Account account;
    public LogoutRequest(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
}
