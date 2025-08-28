package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GetUserFeedbacksResponse;

import java.io.Serializable;

public class ProfileFeedBacks implements Serializable {
    private GetUserFeedbacksResponse response;

    public ProfileFeedBacks(GetUserFeedbacksResponse response) {
        this.response = response;
    }

    public GetUserFeedbacksResponse getResponse() {
        return response;
    }

    public void setResponse(GetUserFeedbacksResponse response) {
        this.response = response;
    }
}
