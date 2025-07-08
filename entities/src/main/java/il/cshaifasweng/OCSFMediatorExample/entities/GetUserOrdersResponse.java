package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class GetUserOrdersResponse implements Serializable {
    private List<OrderSQL> orders;

    public GetUserOrdersResponse(List<OrderSQL> orders) {
        this.orders = orders;
    }

    public List<OrderSQL> getOrders() {
        return orders;
    }
}
