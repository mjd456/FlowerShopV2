package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Feedback implements Serializable {
    private Account account;
    private String feedbackTtitle;
    private String feedbackTdesc;
    private String branch;  // 👈 add branch

    public Feedback(Account account, String feedbackTtitle, String feedbackTdesc, String branch) {
        this.account = account;
        this.feedbackTtitle = feedbackTtitle;
        this.feedbackTdesc = feedbackTdesc;
        this.branch = branch;
    }

    public Account getAccount() {
        return account;
    }

    public String getfeedbackTtitle() {
        return feedbackTtitle;
    }

    public String getfeedbackTdesc() {
        return feedbackTdesc;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}

