package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

// AddFlowerRequest.java
public class AddFlowerRequest implements Serializable {

    private Flower flower;
    private byte[] imageJpeg;
    String suggestedFileName;

    public AddFlowerRequest(Flower newFlower) {
        this.flower = newFlower;
    }


    public AddFlowerRequest(Flower flower, byte[] imageJpeg, String suggestedFileName) {
        this.flower = flower;
        this.imageJpeg = imageJpeg;
        this.suggestedFileName = suggestedFileName;
    }

    public Flower getFlower() {
        return flower;
    }

    public String getSuggestedFileName() {
        return suggestedFileName;
    }
    public byte[] getImageJpeg() {
        return imageJpeg;
    }

}
