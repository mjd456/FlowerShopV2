package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class FeedBackSQL implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedback_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "details", nullable = false, columnDefinition="TEXT")
    private String details;

    // 🔹 New column for branch
    @Column(name = "branch", nullable = false)
    private String branch;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public enum FeedbackStatus {
        Pending,
        Resolved,
        Rejected
    }

    public FeedBackSQL() {}

    public FeedBackSQL(Account account, String title, String details, String branch) {
        this.account = account;
        this.title = title;
        this.details = details;
        this.branch = branch;  // 👈 include branch
        this.status = FeedbackStatus.Pending;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getBranch() {
        return branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }

    // ... keep existing getters/setters

    public int getFeedback_id() {
        return feedback_id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
