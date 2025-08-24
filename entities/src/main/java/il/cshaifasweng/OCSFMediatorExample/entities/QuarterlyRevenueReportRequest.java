package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Date;

public class QuarterlyRevenueReportRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date from;  // inclusive
    private Date to;    // inclusive

    public QuarterlyRevenueReportRequest() { }

    public QuarterlyRevenueReportRequest(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() { return from; }
    public Date getTo() { return to; }

    public void setFrom(Date from) { this.from = from; }
    public void setTo(Date to) { this.to = to; }
}
