package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GetUserFeedbacksRequest implements Serializable {
    private int accountID;
    public GetUserFeedbacksRequest(int accountID) {
        this.accountID = accountID;
    }

    public int getAccountId() {
        return accountID;
    }
}
