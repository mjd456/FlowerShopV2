package il.cshaifasweng.OCSFMediatorExample.client;

public class QuarterlyRevenueRow {
    private final int year;
    private final int quarter;
    private final double revenue;

    public QuarterlyRevenueRow(int year, int quarter, double revenue) {
        this.year = year;
        this.quarter = quarter;
        this.revenue = revenue;
    }

    public int getYear() {
        return year;
    }

    public int getQuarter() {
        return quarter;
    }

    public double getRevenue() {
        return revenue;
    }
}