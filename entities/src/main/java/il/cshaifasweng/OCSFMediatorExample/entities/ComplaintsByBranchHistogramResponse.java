package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class ComplaintsByBranchHistogramResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Row implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int branchId;
        private final String branchName;
        private final long pending;
        private final long resolved;
        private final long rejected;

        public Row(int branchId, String branchName, long pending, long resolved, long rejected) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.pending = pending;
            this.resolved = resolved;
            this.rejected = rejected;
        }

        public int getBranchId() {
            return branchId;
        }

        public String getBranchName() {
            return branchName;
        }

        public long getPending() {
            return pending;
        }

        public long getResolved() {
            return resolved;
        }

        public long getRejected() {
            return rejected;
        }

        public long getTotal() {
            return pending + resolved + rejected;
        }

        /**
         * Helpful for sorting (what needs attention).
         */
        public long getAttention() {
            return pending;
        }
    }

    private final List<Row> rows;

    public ComplaintsByBranchHistogramResponse(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }
}
