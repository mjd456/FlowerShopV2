package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Map;

public class PlaceOrderResponse implements Serializable {
    private final boolean success;
    private final String message;

    public PlaceOrderResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}