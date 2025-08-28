package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.NewFeedbackNotification;

import java.io.Serializable;

public class NewFeedBack implements Serializable {
    private NewFeedbackNotification notification;

    public NewFeedBack(NewFeedbackNotification notification) {
        this.notification = notification;
    }

    public NewFeedbackNotification getNotification() {
        return notification;
    }

    public void setNotification(NewFeedbackNotification notification) {
        this.notification = notification;
    }
}
