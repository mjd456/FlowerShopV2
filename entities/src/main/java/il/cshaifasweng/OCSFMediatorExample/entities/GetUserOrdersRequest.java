package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GetUserOrdersRequest implements Serializable {
    private int accountId;

    public GetUserOrdersRequest(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }
}
