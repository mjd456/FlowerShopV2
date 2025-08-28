package il.cshaifasweng.OCSFMediatorExample.client;

public class ErrorMessageEvent {
    private final String Message;

    public ErrorMessageEvent(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }
}
