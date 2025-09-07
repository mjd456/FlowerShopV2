package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.sql.Date;

public class ComplaintsByBranchHistogramRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Date from;
    private final Date to;

    public ComplaintsByBranchHistogramRequest(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }
}
