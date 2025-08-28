package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.sql.Date;

public class CompareReportsRequest implements Serializable {
    private final int branchId;       // 0 = network, >0 = specific branch
    private final String reportType;  // e.g., "Quarterly Revenue Report"
    private final Date dateA;         // earlier (or first) date
    private final Date dateB;         // later (or second) date

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