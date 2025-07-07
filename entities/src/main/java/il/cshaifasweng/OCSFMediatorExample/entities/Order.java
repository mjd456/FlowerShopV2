package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.ManyToOne;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private Map<Flower, Integer> items;

    private double totalPrice;
    private String greetingText;
    private String deliveryAddress;
    private String deliveryDate;
    private String deliveryTime;

    @ManyToOne
    private Account customer;

    @Column(length = 1000)
    private String itemsSummary;

    // Default constructor for JPA
    public Order() {}

    // Constructor for creating new orders
    public Order(Map<Flower, Integer> items, double totalPrice, String greetingText,
                 String deliveryAddress, String deliveryDate, String deliveryTime, Account customer) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.greetingText = greetingText;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.customer = customer;
        this.itemsSummary = generateItemsSummary();
    }

    private String generateItemsSummary() {
        if (items == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Flower, Integer> entry : items.entrySet()) {
            sb.append(entry.getKey().getName())
                    .append(" x")
                    .append(entry.getValue())
                    .append(", ");
        }
        return sb.toString();
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public Map<Flower, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Flower, Integer> items) {
        this.items = items;
        this.itemsSummary = generateItemsSummary();
    }

    public boolean canBeCancelled() {
        LocalDateTime now = LocalDateTime.now();

        // Parse string to LocalDate and LocalTime
        LocalDate date = LocalDate.parse(this.getDeliveryDate());
        LocalTime time = LocalTime.parse(this.getDeliveryTime());

        LocalDateTime deliveryDateTime = LocalDateTime.of(date, time);

        return now.isBefore(deliveryDateTime.minusMinutes(1));
    }


    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }
}
