package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrdersByProductTypeReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;

        private String productType; // כרגע: שם הפרח/קטגוריה
        private long orders;        // מספר הזמנות
        private long quantity;      // כמות פריטים (אם זמין)
        private double total;       // סך מחיר ההזמנות

        public Row() { }

        public Row(String productType, long orders, long quantity, double total) {
            this.productType = productType;
            this.orders = orders;
            this.quantity = quantity;
            this.total = total;
        }

        public String getProductType() { return productType; }
        public long getOrders() { return orders; }
        public long getQuantity() { return quantity; }
        public double getTotal() { return total; }
    }

    private List<Row> rows = new ArrayList<>();

    public OrdersByProductTypeReportResponse() { }

    public OrdersByProductTypeReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() { return rows; }
}
