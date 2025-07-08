package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "password_history")
public class PasswordHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_current")
    private boolean isCurrent;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SwappedAt")
    private Date swappedAt = new Date();

    public PasswordHistory() {}
    public PasswordHistory(Account user, String passwordHash, boolean isCurrent, Date createdAt) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.isCurrent = isCurrent;
        this.createdAt = createdAt;
        this.swappedAt = null;
    }

    public int getId() {
        return id;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSwappedAt() {
        return swappedAt;
    }
    public void setSwappedAt(Date swappedAt) {
        this.swappedAt = swappedAt;
    }
}
