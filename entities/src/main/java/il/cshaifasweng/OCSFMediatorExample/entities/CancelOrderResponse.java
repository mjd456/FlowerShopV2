package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class CancelOrderResponse implements Serializable {
    private long orderId;
    private boolean cancelled;
    private String message;

    public CancelOrderResponse(long orderId, boolean cancelled, String message) {
        this.orderId = orderId;
        this.cancelled = cancelled;
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getMessage() {
        return message;
    }
}
