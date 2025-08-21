package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Date;

public class OrdersByProductTypeReportRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date from;
    private Date to;

    public OrdersByProductTypeReportRequest() { }

    public OrdersByProductTypeReportRequest(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() { return from; }
    public Date getTo() { return to; }
}
