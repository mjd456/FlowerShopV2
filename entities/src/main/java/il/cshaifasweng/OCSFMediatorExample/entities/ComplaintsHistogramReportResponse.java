package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComplaintsHistogramReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<LocalDate, Long> countsByDay = new LinkedHashMap<>();

    public ComplaintsHistogramReportResponse() { }

    public ComplaintsHistogramReportResponse(Map<LocalDate, Long> countsByDay) {
        this.countsByDay = countsByDay;
    }

    public Map<LocalDate, Long> getCountsByDay() {
        return countsByDay;
    }
}