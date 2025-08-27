package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuarterlyRevenueReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;

        private int year;
        private int quarter;   // 1..4
        private double revenue;
        private int branchId;  // 0 = network, 1 = Haifa, 2 = Eilat, 3 = Tel Aviv

        public Row() { }

        public Row(int year, int quarter, double revenue) {
            this.year = year;
            this.quarter = quarter;
            this.revenue = revenue;
            this.branchId = 0; // default = network
        }

        public Row(int year, int quarter, int branchId, double revenue) {
            this.year = year;
            this.quarter = quarter;
            this.branchId = branchId;
            this.revenue = revenue;
        }

        public int getYear() { return year; }
        public int getQuarter() { return quarter; }
        public double getRevenue() { return revenue; }
        public int getBranchId() { return branchId; }

        public String getBranchName() {
            switch (branchId) {
                case 1: return "Haifa";
                case 2: return "Eilat";
                case 3: return "Tel Aviv";
                default: return "Network";
            }
        }
    }

    private List<Row> rows = new ArrayList<>();

    public QuarterlyRevenueReportResponse() { }

    public QuarterlyRevenueReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() { return rows; }
}
