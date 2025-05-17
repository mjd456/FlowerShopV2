package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

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
    private int supply;

    @Column(nullable = false)
    private int popularity = 0;

    // ----- Constructors -----

    public Flower() {}

    public Flower(String name, String color, String description, String imageId, double price, int supply) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageId = imageId;
        this.price = price;
        this.supply = supply;
        this.popularity = 0;
    }

    // ----- Getters and Setters -----

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void incrementPopularity() {
        this.popularity++;
    }
}
