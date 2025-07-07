package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class UpdateFlowerSupplyRequest implements Serializable {
    private List<Flower> updatedFlowers;

    public UpdateFlowerSupplyRequest(List<Flower> updatedFlowers) {
        this.updatedFlowers = updatedFlowers;
    }

    public List<Flower> getUpdatedFlowers() {
        return updatedFlowers;
    }
}
