package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuarterlyRevenueReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;
        private int year;
        private int quarter;
        private double revenue;
        private int branchId;
        public Row() { }
        public Row(int year, int quarter, double revenue) {
            this.year = year; this.quarter = quarter; this.revenue = revenue; this.branchId = 0;
        }
        public Row(int year, int quarter, int branchId, double revenue) {
            this.year = year; this.quarter = quarter; this.branchId = branchId; this.revenue = revenue;
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

    public static class BreakdownRow implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Integer branchId;
        private final String branch;
        private final String type;
        private final double revenue;

        public BreakdownRow(Integer branchId, String branch, String type, double revenue) {
            this.branchId = branchId; this.branch = branch; this.type = type; this.revenue = revenue;
        }
        public Integer getBranchId() { return branchId; }
        public String  getBranch()   { return branch; }
        public String  getType()     { return type; }
        public double  getRevenue()  { return revenue; }
    }

    private List<Row> rows = new ArrayList<>();
    private List<BreakdownRow> breakdown = new ArrayList<>();  // <-- NEW

    public QuarterlyRevenueReportResponse() { }
    public QuarterlyRevenueReportResponse(List<Row> rows) { this.rows = rows; }

    public List<Row> getRows() { return rows; }
    public List<BreakdownRow> getBreakdown() { return breakdown; }  // <-- NEW

    public void setBreakdown(List<BreakdownRow> breakdown) {
        this.breakdown = (breakdown != null ? breakdown : new ArrayList<>());
    }
}