package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

public class PlaceOrderRequest implements Serializable {
    private final Map<Flower, Integer> cartMap;
    private final Account customer;
    private final LocalDate date;
    private final String time;
    private final String status;
    private final String details;
    private final double totalPrice;
    private final String addressOrPickup;
    private final String greeting;

    private final Integer pickupBranchId;

    public PlaceOrderRequest(
            Map<Flower, Integer> cartMap,
            Account customer,
            LocalDate date,
            String time,
            String status,
            String details,
            double totalPrice,
            String addressOrPickup,
            String greeting,
            Integer pickupBranchId
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
        this.pickupBranchId = pickupBranchId;
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
    public Integer getPickupBranchId() { return pickupBranchId; } // <--- NEW
}
