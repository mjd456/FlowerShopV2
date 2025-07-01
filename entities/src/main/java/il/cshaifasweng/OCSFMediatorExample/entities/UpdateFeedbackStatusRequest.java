package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class UpdateFeedbackStatusRequest implements Serializable {
    private int feedbackId;
    private FeedBackSQL.FeedbackStatus status;

    public UpdateFeedbackStatusRequest(int feedbackId, FeedBackSQL.FeedbackStatus status) {
        this.feedbackId = feedbackId;
        this.status = status;
    }
    public int getFeedbackId() { return feedbackId; }
    public FeedBackSQL.FeedbackStatus getStatus() { return status; }
}
