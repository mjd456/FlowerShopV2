package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

public class ErrorMessageEvent {
    private final String Message;

    public ErrorMessageEvent(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }
}
