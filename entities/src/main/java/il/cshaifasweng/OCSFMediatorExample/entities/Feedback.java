package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Feedback implements Serializable {
    private Account account;
    private String feedbackTtitle;
    private String feedbackTdesc;

    public Feedback(Account account, String feedbackTtitle,String feedbackTdesc) {
        this.account = account;
        this.feedbackTtitle = feedbackTtitle;
        this.feedbackTdesc = feedbackTdesc;
    }

    public Account getAccount() {
        return account;
    }
    public String getfeedbackTtitle() {return  feedbackTtitle;}
    public String getfeedbackTdesc() {
        return feedbackTdesc;
    }
}
