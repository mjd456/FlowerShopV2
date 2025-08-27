package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "flowers")
public class Flower implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_id")
    private String imageId;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int supply;  // total supply (sum of the 3 below)

    @Column(nullable = false)
    private int popularity = 0;

    @Column(name = "Supply_Haifa", nullable = false)
    private int supplyHaifa = 0;

    @Column(name = "Supply_Eilat", nullable = false)
    private int supplyEilat = 0;

    @Column(name = "Supply_TelAviv", nullable = false)
    private int supplyTelAviv = 0;

    @Column(name = "Storage", nullable = false)
    private int storage;

    @PreUpdate
    @PrePersist
    public void syncSupply() {
        this.supply = storage + supplyHaifa + supplyEilat + supplyTelAviv;
    }


    // ----- Constructors -----

    public Flower() {}

    public Flower(String name, String color, String description, String imageId,
                  double price, int supply, int supplyHaifa, int supplyEilat, int supplyTelAviv,int storage) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageId = imageId;
        this.price = price;
        this.supply = supply;
        this.supplyHaifa = supplyHaifa;
        this.supplyEilat = supplyEilat;
        this.supplyTelAviv = supplyTelAviv;
        this.popularity = 0;
        this.storage = storage;
    }

    // ----- Getters and Setters -----

    // --- Branch-aware supply helpers ---
    public int getSupply(String branch) {
        switch (branch) {
            case "Haifa": return supplyHaifa;
            case "Eilat": return supplyEilat;
            case "Tel Aviv": return supplyTelAviv;
            case "Storage": return storage;
            default: return 0;
        }
    }



    public void reduceSupply(String branch, int amount) {
        switch (branch) {
            case "Haifa":
                supplyHaifa = Math.max(0, supplyHaifa - amount);
                break;
            case "Eilat":
                supplyEilat = Math.max(0, supplyEilat - amount);
                break;
            case "Tel Aviv":
                supplyTelAviv = Math.max(0, supplyTelAviv - amount);
                break;
        }
    }

    public void reduceSupplyForDelivery(int amount) {
        while (amount > 0) {
            // Find the branch with the maximum supply
            if (supplyHaifa >= supplyEilat && supplyHaifa >= supplyTelAviv && supplyHaifa > 0) {
                supplyHaifa--;
            } else if (supplyEilat >= supplyHaifa && supplyEilat >= supplyTelAviv && supplyEilat > 0) {
                supplyEilat--;
            } else if (supplyTelAviv >= supplyHaifa && supplyTelAviv >= supplyEilat && supplyTelAviv > 0) {
                supplyTelAviv--;
            } else {
                // No stock left anywhere
                break;
            }
            amount--; // Reduce requested amount after each deduction
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flower)) return false;
        Flower flower = (Flower) o;
        return id == flower.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    private void recalcTotalSupply() {
        this.supply = supplyHaifa + supplyEilat + supplyTelAviv;
    }


    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageId() { return imageId; }
    public void setImageId(String imageId) { this.imageId = imageId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getSupply() { return supply; }
    public void setSupply(int supply) { this.supply = supply; }

    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    public void incrementPopularity() { this.popularity++; }

    public int getSupplyHaifa() { return supplyHaifa; }
    public void setSupplyHaifa(int supplyHaifa) { this.supplyHaifa = supplyHaifa; }

    public int getSupplyEilat() { return supplyEilat; }
    public void setSupplyEilat(int supplyEilat) { this.supplyEilat = supplyEilat; }

    public int getSupplyTelAviv() { return supplyTelAviv; }
    public void setSupplyTelAviv(int supplyTelAviv) { this.supplyTelAviv = supplyTelAviv; }

    public int getStorage() { return storage; }
    public void setStorage(int storage) { this.storage = Math.max(0, storage); }

}
