package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.sql.Date;

public class CompareReportsRequest implements Serializable {
    private final int branchId;
    private final String reportType;
    private final Date dateA;
    private final Date dateB;

    public CompareReportsRequest(int branchId, String reportType, Date dateA, Date dateB) {
        this.branchId = branchId;
        this.reportType = reportType;
        this.dateA = dateA;
        this.dateB = dateB;
    }

    public int getBranchId()     { return branchId; }
    public String getReportType(){ return reportType; }
    public Date getDateA()       { return dateA; }
    public Date getDateB()       { return dateB; }
}