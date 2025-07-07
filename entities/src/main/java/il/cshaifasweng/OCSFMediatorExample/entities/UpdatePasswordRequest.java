package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class UpdatePasswordRequest implements Serializable {
    private String newPass;

    public UpdatePasswordRequest(String newPass) {
        this.newPass = newPass;
    }

    public String getNewPass() {
        return newPass;
    }
}
