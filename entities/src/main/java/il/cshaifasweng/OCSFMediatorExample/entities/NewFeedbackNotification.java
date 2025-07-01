package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class NewFeedbackNotification implements Serializable {
    private FeedBackSQL feedback;
    public NewFeedbackNotification(FeedBackSQL feedback) { this.feedback = feedback; }
    public FeedBackSQL getFeedback() { return feedback; }
}
