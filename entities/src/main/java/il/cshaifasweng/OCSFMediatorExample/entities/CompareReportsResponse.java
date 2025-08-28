package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CompareReportsResponse implements Serializable {

    public static class Row implements Serializable {
        private final String metric;
        private final Number valueA;
        private final Number valueB;
        private final Number delta;

        public Row(String metric, Number valueA, Number valueB) {
            this.metric = metric;
            this.valueA = valueA;
            this.valueB = valueB;
            this.delta = (valueA != null && valueB != null)
                    ? valueB.doubleValue() - valueA.doubleValue()
                    : null;
        }

        public String getMetric() { return metric; }
        public Number getValueA() { return valueA; }
        public Number getValueB() { return valueB; }
        public Number getDelta()  { return delta; }
    }

    private final Date dateA;
    private final Date dateB;
    private final String branchName;
    private final String reportType;
    private final List<Row> rows = new ArrayList<>();

    public CompareReportsResponse(Date dateA, Date dateB, String branchName, String reportType) {
        this.dateA = dateA;
        this.dateB = dateB;
        this.branchName = branchName;
        this.reportType = reportType;
    }

    public void addRow(String metric, Number a, Number b) {
        rows.add(new Row(metric, a, b));
    }

    public Date getDateA()          { return dateA; }
    public Date getDateB()          { return dateB; }
    public String getBranchName()   { return branchName; }
    public String getReportType()   { return reportType; }
    public List<Row> getRows()      { return rows; }
}
