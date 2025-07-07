package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class FlowerDeleted implements Serializable {
    private int flowerId;
    public FlowerDeleted(int flowerId) { this.flowerId = flowerId; }
    public int getFlowerId() { return flowerId; }
}
