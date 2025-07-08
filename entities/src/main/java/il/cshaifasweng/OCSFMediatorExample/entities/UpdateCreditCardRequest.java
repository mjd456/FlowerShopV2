package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Date;

public class UpdateCreditCardRequest implements Serializable {
    private int accountId;
    private String creditCardNumber;
    private String ccv;
    private Date validUntil;

    public UpdateCreditCardRequest(int accountId, String creditCardNumber, String ccv, Date validUntil) {
        this.accountId = accountId;
        this.creditCardNumber = creditCardNumber;
        this.ccv = ccv;
        this.validUntil = validUntil;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCcv() {
        return ccv;
    }

    public Date getValidUntil() {
        return validUntil;
    }
}
