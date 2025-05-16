package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean success;
    private int accountId;
    private String accountName;
    private Account account;

    // Constructor
    public LoginResponse(boolean success, int accountId, String accountName, Account account) {
        this.success = success;
        this.accountId = accountId;
        this.accountName = accountName;
        this.account = account;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }
    public Account getAccount() {
        return account;
    }
}
