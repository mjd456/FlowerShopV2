package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.beans.property.*;

public class RevenueRow {
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty quarter = new SimpleIntegerProperty();
    private final DoubleProperty revenue = new SimpleDoubleProperty();

    public RevenueRow(int year, int quarter, double revenue) {
        this.year.set(year);
        this.quarter.set(quarter);
        this.revenue.set(revenue);
    }

    public int getYear() { return year.get(); }
    public IntegerProperty yearProperty() { return year; }

    public int getQuarter() { return quarter.get(); }
    public IntegerProperty quarterProperty() { return quarter; }

    public double getRevenue() { return revenue.get(); }
    public DoubleProperty revenueProperty() { return revenue; }
}
