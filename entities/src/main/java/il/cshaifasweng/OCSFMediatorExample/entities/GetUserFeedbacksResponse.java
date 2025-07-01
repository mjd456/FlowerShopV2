package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class GetUserFeedbacksResponse implements Serializable {
    private List<FeedBackSQL> feedbacks;

    public GetUserFeedbacksResponse(List<FeedBackSQL> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<FeedBackSQL> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedBackSQL> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
