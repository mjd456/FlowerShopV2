package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

// AddFlowerRequest.java
public class AddFlowerRequest implements Serializable {
    private final Flower flower;

    // NEW:
    private final byte[] imageJpeg;           // may be null if picture optional
    private final String suggestedFileName;   // e.g. "rose.jpg"

    public AddFlowerRequest(Flower flower, byte[] imageJpeg, String suggestedFileName) {
        this.flower = flower;
        this.imageJpeg = imageJpeg;
        this.suggestedFileName = suggestedFileName;
    }

    public Flower getNewFlower() { return flower; }
    public byte[] getImageJpeg() { return imageJpeg; }
    public String getSuggestedFileName() { return suggestedFileName; }

}
