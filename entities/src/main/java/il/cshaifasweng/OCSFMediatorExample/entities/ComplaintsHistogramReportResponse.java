package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComplaintsHistogramReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // key: status name (e.g. "OPEN", "IN_PROGRESS", "RESOLVED"), value: count
    private Map<String, Long> counts = new LinkedHashMap<>();

    public ComplaintsHistogramReportResponse() { }

    public ComplaintsHistogramReportResponse(Map<String, Long> counts) {
        this.counts = counts;
    }

    public Map<String, Long> getCounts() { return counts; }
}
