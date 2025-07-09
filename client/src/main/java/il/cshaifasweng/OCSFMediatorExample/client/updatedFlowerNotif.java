package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;

public class updatedFlowerNotif {
    private Flower updatedFlower;
    public updatedFlowerNotif(Flower updatedFlower) {
        this.updatedFlower = updatedFlower;
        System.out.println("In constructor");

    }
    public Flower getUpdatedFlower() {
        return updatedFlower;
    }
}
