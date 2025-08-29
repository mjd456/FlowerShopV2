package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

// AddFlowerRequest.java
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


    public AddFlowerRequest(Flower flower, byte[] imageJpeg, String suggestedFileName) {
        this.flower = flower;
        this.imageJpeg = imageJpeg;
        this.suggestedFileName = suggestedFileName;
    }

    public String getSuggestedFileName() {
        return suggestedFileName;
    }
    public byte[] getImageJpeg() {
        return newImageJpeg;
    }

}
