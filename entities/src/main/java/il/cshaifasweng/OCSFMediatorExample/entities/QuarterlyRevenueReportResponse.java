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

        public Row() { }

        public Row(int year, int quarter, double revenue) {
            this.year = year;
            this.quarter = quarter;
            this.revenue = revenue;
        }

        public int getYear() { return year; }
        public int getQuarter() { return quarter; }
        public double getRevenue() { return revenue; }
    }

    private List<Row> rows = new ArrayList<>();

    public QuarterlyRevenueReportResponse() { }

    public QuarterlyRevenueReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() { return rows; }
}
