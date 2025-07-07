package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class DeleteFlowerRequest implements Serializable {
    private int flowerId;
    public DeleteFlowerRequest(int flowerId) { this.flowerId = flowerId; }
    public int getFlowerId() { return flowerId; }
}
