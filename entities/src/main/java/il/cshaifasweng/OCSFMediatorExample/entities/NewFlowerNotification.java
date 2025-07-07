package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class NewFlowerNotification implements Serializable {
    private Flower flower;

    public NewFlowerNotification(Flower flower) {
        this.flower = flower;
    }

    public Flower getFlower() {
        return flower;
    }
}
