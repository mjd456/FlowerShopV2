package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AddFlowerRequest implements Serializable {
    private Flower newFlower;

    public AddFlowerRequest(Flower newFlower) {
        this.newFlower = newFlower;
    }

    public Flower getNewFlower() {
        return newFlower;
    }
}
