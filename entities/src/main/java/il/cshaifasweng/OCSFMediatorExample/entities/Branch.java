package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Branches") // matches your MySQL table name exactly
public class Branch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;  // "Haifa", "Eilat", "TelAviv"

    @Column(name = "total_flowers", nullable = false)
    private int totalFlowers = 0; // sum from Flowers.Supply_* for this branch

    // ===== Constructors =====
    public Branch() {}

    public Branch(String name) {
        this.name = name;
    }

    public Branch(String name, int totalFlowers) {
        this.name = name;
        this.totalFlowers = totalFlowers;
    }

    // ===== Getters / Setters =====
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTotalFlowers() { return totalFlowers; }
    public void setTotalFlowers(int totalFlowers) { this.totalFlowers = totalFlowers; }

    // ===== equals / hashCode =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Branch)) return false;
        Branch branch = (Branch) o;
        return id == branch.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Branch{id=" + id + ", name='" + name + '\'' +
                ", totalFlowers=" + totalFlowers + '}';
    }
}
