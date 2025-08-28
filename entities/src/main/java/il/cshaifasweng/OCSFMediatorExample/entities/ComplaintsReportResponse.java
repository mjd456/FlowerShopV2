package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class ComplaintsReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;
        private final LocalDate day;
        private final long count;

        public Row(LocalDate day, long count) {
            this.day = day;
            this.count = count;
        }
        public LocalDate getDay() { return day; }
        public long getCount() { return count; }
    }

    private final List<Row> rows;

    public ComplaintsReportResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() { return rows; }
}