package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GetAllFeedbacksResponse;

public class AllFeedBackInfo {
    private GetAllFeedbacksResponse response;

    public AllFeedBackInfo(GetAllFeedbacksResponse response) {
        this.response = response;
    }

    public GetAllFeedbacksResponse getResponse() {
        return response;
    }

    public void setResponse(GetAllFeedbacksResponse response) {
        this.response = response;
    }
}
