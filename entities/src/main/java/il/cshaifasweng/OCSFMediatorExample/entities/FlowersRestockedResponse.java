package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class FlowersRestockedResponse implements Serializable {
    private List<Flower> flowers;

    public FlowersRestockedResponse(List<Flower> flowers) {
        this.flowers = flowers;
    }
    public List<Flower> getFlowers() { return flowers; }
}
