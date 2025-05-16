package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

public class ErrorMessageSignUpEvent {
    private final String Message;

    public ErrorMessageSignUpEvent(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }
}
