package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AddFlowerRequest implements Serializable {
    private Flower newFlower;
    private byte[] newImageJpeg;
    String suggestedFileName;

    public AddFlowerRequest(Flower newFlower) {
        this.newFlower = newFlower;
    }
    public AddFlowerRequest(Flower flower, byte[] imageJpeg, String suggestedFileName) {
        this.newFlower = flower;
        this.newImageJpeg = imageJpeg;
        this.suggestedFileName = suggestedFileName;
    }

    public Flower getNewFlower() {
        return newFlower;
    }

    public String getSuggestedFileName() {
        return suggestedFileName;
    }
    public byte[] getImageJpeg() {
        return newImageJpeg;
    }
}
