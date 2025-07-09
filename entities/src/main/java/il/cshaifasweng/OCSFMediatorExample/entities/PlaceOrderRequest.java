package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

public class PlaceOrderRequest implements Serializable {
    private final Map<Flower, Integer> cartMap;
    private final Account customer;
    private final LocalDate date;    // Or use java.sql.Date if you prefer
    private final String time;
    private final String status;
    private final String details;
    private final double totalPrice;
    private final String addressOrPickup;
    private final String greeting;

    public PlaceOrderRequest(
            Map<Flower, Integer> cartMap,
            Account customer,
            LocalDate date,
            String time,
            String status,
            String details,
            double totalPrice,
            String addressOrPickup,
            String greeting
    ) {
        this.cartMap = cartMap;
        this.customer = customer;
        this.date = date;
        this.time = time;
        this.status = status;
        this.details = details;
        this.totalPrice = totalPrice;
        this.addressOrPickup = addressOrPickup;
        this.greeting = greeting;
    }

    public Map<Flower, Integer> getCartMap() { return cartMap; }
    public Account getCustomer() { return customer; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getDetails() { return details; }
    public double getTotalPrice() { return totalPrice; }
    public String getAddressOrPickup() { return addressOrPickup; }
    public String getGreeting() { return greeting; }
}
