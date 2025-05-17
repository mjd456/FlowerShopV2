package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;

import java.util.Map;

public class FlowerListEventBus {
    private Map<Flower, byte[]> flowerMap;

    public FlowerListEventBus(Map<Flower, byte[]> flowerMap) {
        this.flowerMap = flowerMap;
    }

    public Map<Flower, byte[]> getFlowerMap() {
        return flowerMap;
    }

    public void setFlowerMap(Map<Flower, byte[]> flowerMap) {
        this.flowerMap = flowerMap;
    }
}
