package il.cshaifasweng.OCSFMediatorExample.client;

public class ChangingPasswordError {
    String errorMessage;
    public ChangingPasswordError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
