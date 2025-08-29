package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import java.io.Serializable;

public class UpdateFlowerRequest implements Serializable {
    private Flower flower;
    private byte[] newImageJpeg;        // nullable
    private String suggestedFileName;   // nullable
    private boolean deleteImage;        // NEW

    public UpdateFlowerRequest(Flower flower) { this(flower, null, null, false); }
    public UpdateFlowerRequest(Flower flower, byte[] newImageJpeg, String suggestedFileName) {
        this(flower, newImageJpeg, suggestedFileName, false);
    }
    public UpdateFlowerRequest(Flower flower, byte[] newImageJpeg,
                               String suggestedFileName, boolean deleteImage) {
        this.flower = flower;
        this.newImageJpeg = newImageJpeg;
        this.suggestedFileName = suggestedFileName;
        this.deleteImage = deleteImage;
    }

    public Flower getFlower() { return flower; }
    public byte[] getNewImageJpeg() { return newImageJpeg; }
    public String getSuggestedFileName() { return suggestedFileName; }
    public boolean isDeleteImage() { return deleteImage; }
}
