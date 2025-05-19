package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;

import java.io.Serializable;

public class UpdateFlowerRequest implements Serializable {
    private Flower flower;
    // constructor, getter, setter
    public UpdateFlowerRequest(Flower flower) {
        this.flower = flower;
    }
    public Flower getFlower() {
        return flower;
    }
    public void setFlower(Flower flower) {
        this.flower = flower;
    }
}
