package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;
import java.sql.Date;

public class ComplaintsReportRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Date from;
    private final Date to;
    private final int branchId; // 0 = Network (all)

    public ComplaintsReportRequest(Date from, Date to, int branchId) {
        this.from = from;
        this.to = to;
        this.branchId = branchId;
    }

    public Date getFrom() { return from; }
    public Date getTo() { return to; }
    public int getBranchId() { return branchId; }
}
