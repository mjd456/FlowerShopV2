package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ComplaintsReportResponse implements Serializable {

    public static class Detail implements Serializable {
        private final int id;
        private final String title;
        private final String details;
        private final String email;
        private final String branch;
        private final LocalDateTime submittedAt;
        private final FeedBackSQL.FeedbackStatus status;

        public Detail(int id, String title, String details, String email, String branch,
                      LocalDateTime submittedAt, FeedBackSQL.FeedbackStatus status) {
            this.id = id;
            this.title = title;
            this.details = details;
            this.email = email;
            this.branch = branch;
            this.submittedAt = submittedAt;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDetails() {
            return details;
        }

        public String getEmail() {
            return email;
        }

        public String getBranch() {
            return branch;
        }

        public LocalDateTime getSubmittedAt() {
            return submittedAt;
        }

        public FeedBackSQL.FeedbackStatus getStatus() {
            return status;
        }
    }

    public static class Row implements Serializable {
        private final LocalDate day;
        private final long count;
        private final List<Detail> details;

        public Row(LocalDate day, long count, List<Detail> details) {
            this.day = day;
            this.count = count;
            this.details = details;
        }

        public LocalDate getDay() {
            return day;
        }

        public long getCount() {
            return count;
        }

        public List<Detail> getDetails() {
            return details;
        }
    }

    private final List<Row> rows;

    public ComplaintsReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }
}