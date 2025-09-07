package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class OrderDetailsController {

    @FXML
    private ComboBox<String> BranchComboBox;

    @FXML
    private Label BranchLabel;

    @FXML
    private RadioButton DeliveryRadio;

    @FXML
    private RadioButton PickupRadio;

    @FXML
    private DatePicker DatePicker;

    @FXML
    private ComboBox<String> TimeComboBox;

    @FXML
    private TextField AddressField;

    @FXML
    private TextField GreetingField;

    @FXML
    private Label DateStar;

    @FXML
    private Label TimeStar;

    @FXML
    private Label AddressStar;

    private ToggleGroup deliveryGroup;
    private Map<Flower, Integer> cartMap;
    private double totalPrice;
    private Account customer;

    public void setOrderData(Map<Flower, Integer> cartMap, double totalPrice, Account customer) {
        this.cartMap = cartMap;
        this.totalPrice = totalPrice;
        this.customer = customer;
    }

    @FXML
    public void initialize() {
        // Setup radio button group
        deliveryGroup = new ToggleGroup();
        DeliveryRadio.setToggleGroup(deliveryGroup);
        PickupRadio.setToggleGroup(deliveryGroup);
        DeliveryRadio.setSelected(true);

        // Add branch options
        BranchComboBox.getItems().addAll("Haifa", "Eilat", "Tel Aviv");

        DatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateAvailableTimes(newDate);
        });

        // Setup time slots
        TimeComboBox.getItems().addAll(Arrays.asList("09:00", "12:00", "15:00", "18:00"));

        // Enable/disable fields depending on delivery type
        deliveryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (PickupRadio.isSelected()) {
                AddressField.setDisable(true);
                BranchLabel.setVisible(true);
                BranchComboBox.setVisible(true);
            } else {
                AddressField.setDisable(false);
                BranchLabel.setVisible(false);
                BranchComboBox.setVisible(false);
            }

            // Clear fields
            DatePicker.setValue(null);
            TimeComboBox.setValue(null);
            AddressField.clear();
            GreetingField.clear();

            // Clear error stars
            DateStar.setVisible(false);
            TimeStar.setVisible(false);
            AddressStar.setVisible(false);

            // Reset time options
            updateAvailableTimes(null);
        });

        // Initially hide branch selector
        BranchLabel.setVisible(false);
        BranchComboBox.setVisible(false);

        updateAvailableTimes(DatePicker.getValue());
    }

    private void updateAvailableTimes(java.time.LocalDate selectedDate) {
        TimeComboBox.getItems().clear();

        var now = java.time.LocalTime.now();
        var allTimes = Arrays.asList("09:00", "12:00", "15:00", "18:00");

        if (selectedDate != null && selectedDate.isEqual(java.time.LocalDate.now())) {
            for (String timeStr : allTimes) {
                java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
                if (time.isAfter(now)) {
                    TimeComboBox.getItems().add(timeStr);
                }
            }
        } else {
            // If not today or null, show all
            TimeComboBox.getItems().addAll(allTimes);
        }

        // Clear selection
        TimeComboBox.setValue(null);
    }

    @FXML
    private void confirmOrder() throws IOException {
        var date = DatePicker.getValue();
        var time = TimeComboBox.getValue();
        var address = AddressField.getText().trim();
        var greeting = GreetingField.getText().trim();
        var status = "upcoming";

        boolean valid = true;

        // reset stars
        DateStar.setVisible(false);
        TimeStar.setVisible(false);
        AddressStar.setVisible(false);

        if (date == null) {
            DateStar.setVisible(true);
            valid = false;
        }
        if (time == null || time.isEmpty()) {
            TimeStar.setVisible(true);
            valid = false;
        }
        if (!PickupRadio.isSelected() && address.isEmpty()) {
            AddressStar.setVisible(true);
            valid = false;
        }

        if (!valid) {
            showWarning("Please fill in all required fields!");
            return;
        }

        var today = java.time.LocalDate.now();
        var nowTime = java.time.LocalTime.now();

        if (date.isBefore(today)) {
            showWarning("Please select a valid future date.");
            return;
        } else if (date.isEqual(today)) {
            var selectedTime = java.time.LocalTime.parse(time);
            if (!selectedTime.isAfter(nowTime)) {
                showWarning("Please select a future time today.");
                return;
            }
        }

        // --- Branch validation & stock updates ---
        String pickupBranchName = null;
        int pickupBranchId = 4; // default for delivery

        if (PickupRadio.isSelected()) {
            pickupBranchName = BranchComboBox.getValue();
            if (pickupBranchName == null || pickupBranchName.isEmpty()) {
                showWarning("Please select a branch for pickup.");
                return;
            }

            pickupBranchId = mapBranchNameToId(pickupBranchName); // 1/2/3 based on name

            // check stock at that branch
            for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
                Flower flower = entry.getKey();
                int requested = entry.getValue();
                int available = flower.getSupply(pickupBranchName);
                if (requested > available) {
                    showWarning("Branch " + pickupBranchName + " does not have enough " + flower.getName() + ".");
                    return;
                }
            }

            // reduce stock at that branch
            for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
                Flower flower = entry.getKey();
                int requested = entry.getValue();
                flower.reduceSupply(pickupBranchName, requested);
                SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
            }
        } else {
            for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
                Flower flower = entry.getKey();
                System.out.println("No pickup branch selected. = " + entry.getValue());
                int requested = entry.getValue();
                flower.reduceSupplyForDelivery(requested);
                SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
            }
        }

        // details string
        StringBuilder details = new StringBuilder();
        for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
            details.append(entry.getKey().getName())
                    .append(" x")
                    .append(entry.getValue())
                    .append(", ");
        }
        if (details.length() > 2) details.setLength(details.length() - 2);

        String addrOrPickup = PickupRadio.isSelected()
                ? pickupBranchName + " Pickup"
                : address;

        PlaceOrderRequest request = new PlaceOrderRequest(
                cartMap,
                customer,
                date,
                time,
                status,
                details.toString(),
                totalPrice,
                addrOrPickup,
                greeting,
                pickupBranchId
        );
        System.out.println("=== CartMap before sending ===");
        for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
            System.out.println("Flower: " + entry.getKey().getName()
                    + " | Qty: " + entry.getValue()
                    + " | Price: " + entry.getKey().getPrice()
                    + " | Discount: " + entry.getKey().getDiscount());
        }
        System.out.println("================================");
        SimpleClient.getClient().sendToServer(request);

        cartMap.clear();
        SecondaryController.instance.showCart();

        Stage stage = (Stage) GreetingField.getScene().getWindow();
        stage.close();
    }

    /**
     * Map branch display name to DB id. Defaults to 0 if unknown.
     */
    private static int mapBranchNameToId(String name) {
        if (name == null) return 0;
        switch (name.trim().toLowerCase()) {
            case "haifa":
                return 1;
            case "eilat":
                return 2;
            case "tel aviv":
            case "telaviv":
            case "tel-aviv":
                return 3;
            default:
                return 0;
        }
    }

    @Subscribe
    public void onPlaceOrderResponse(PlaceOrderResponse response) {
        Platform.runLater(() -> {
        });
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }

        alert.showAndWait();
    }
}
