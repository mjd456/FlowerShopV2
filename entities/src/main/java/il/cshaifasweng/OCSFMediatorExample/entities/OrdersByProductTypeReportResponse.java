package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrdersByProductTypeReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;

        private String productType;
        private long orders;
        private long quantity;
        private double total;

        public Row() {
        }

        public Row(String productType, long orders, long quantity, double total) {
            this.productType = productType;
            this.orders = orders;
            this.quantity = quantity;
            this.total = total;
        }

        public String getProductType() {
            return productType;
        }

        public long getOrders() {
            return orders;
        }

        public long getQuantity() {
            return quantity;
        }

        public double getTotal() {
            return total;
        }
    }

    private List<Row> rows = new ArrayList<>();

    public OrdersByProductTypeReportResponse() {
    }

    public OrdersByProductTypeReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }
}
