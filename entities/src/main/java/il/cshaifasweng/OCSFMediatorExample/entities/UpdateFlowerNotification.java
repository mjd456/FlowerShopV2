package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;

public class UpdateFlowerNotification implements Serializable {
    private Flower flower;
    private byte[] imageData; // optional, include if you send updated image

    public UpdateFlowerNotification(Flower flower) {
        this.flower = flower;
    }

    public UpdateFlowerNotification(Flower flower, byte[] imageData) {
        this.flower = flower;
        this.imageData = imageData;
    }

    public Flower getFlower() { return flower; }
    public byte[] getImageData() { return imageData; }
}
