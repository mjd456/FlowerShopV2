package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "orders") // Assuming the table is named 'orders'
public class OrderSQL implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Each order is for a single account; an account can have many orders
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // in OrderSQL
    @Column(name = "delivery_date")
    private Date deliveryDate; // <- no @Temporal

    @Column(name = "delivery_time")
    private String deliveryTime;

    @Column(name = "status")
    private String status;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "address")
    private String address;

    @Column(name = "greeting_text")
    private String greetingText;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "pickup_branch")
    private Integer pickupBranch;

    // ===== Constructors =====
    public OrderSQL() {}

    public OrderSQL(Account account, Date deliveryDate, String deliveryTime,
                    String status, String details, Double totalPrice,
                    String address, String greetingText, Integer pickupBranch) {
        this.account = account;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.status = status;
        this.details = details;
        this.totalPrice = totalPrice;
        this.address = address;
        this.greetingText = greetingText;
        this.refundAmount = 0.0;
        this.pickupBranch = pickupBranch;
    }

    // ===== Getters and Setters =====

    public long getId() { return id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGreetingText() { return greetingText; }
    public void setGreetingText(String greetingText) { this.greetingText = greetingText; }

    public Double getRefundAmount() { return refundAmount; }

    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public Integer getPickupBranch() {
        return pickupBranch;
    }

    public void setPickupBranch(Integer pickupBranch) {
        this.pickupBranch = pickupBranch;
    }

}