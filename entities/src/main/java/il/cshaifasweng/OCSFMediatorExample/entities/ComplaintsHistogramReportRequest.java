package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Date;

public class ComplaintsHistogramReportRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date from;
    private Date to;
    private int branchId; // 0 for all branches

    public ComplaintsHistogramReportRequest(Date from, Date to, int branchId) {
        this.from = from;
        this.to = to;
        this.branchId = branchId;
    }

    public Date getFrom() { return from; }
    public Date getTo() { return to; }
    public int getBranchId() { return branchId; }
}