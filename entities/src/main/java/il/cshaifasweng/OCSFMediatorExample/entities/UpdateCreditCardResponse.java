package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.sql.Date;

public class UpdateCreditCardResponse implements Serializable {
    private boolean success;
    private String message;
    private Account account;

    public UpdateCreditCardResponse(boolean success, String message, Account account) {
        this.success = success;
        this.message = message;
        this.account = account;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public Account getAccount() {
        return account;
    }
}

