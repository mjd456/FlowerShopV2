package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;
import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Alert;
import il.cshaifasweng.OCSFMediatorExample.entities.UpdateFlowerSupplyRequest;
import java.util.ArrayList;



public class OrderDetailsController {

    @FXML
    private TextField GreetingField;

    @FXML
    private TextField AddressField;

    @FXML
    private DatePicker DatePicker;

    @FXML
    private TextField TimeField;

    private Map<Flower, Integer> cartMap;
    private double totalPrice;
    private Account customer;

    public void setOrderData(Map<Flower, Integer> cartMap, double totalPrice, Account customer) {
        this.cartMap = cartMap;
        this.totalPrice = totalPrice;
        this.customer = customer;
    }

    @FXML
    private void confirmOrder() {
        String greeting = GreetingField.getText();
        String address = AddressField.getText();
        String date = DatePicker.getValue() != null ? DatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        String time = TimeField.getText();

        Order order = new Order(
                new HashMap<>(cartMap),
                totalPrice,
                greeting,
                address,
                date,
                time,
                customer
        );

        System.out.println("=== Order CONFIRMED ===");
        System.out.println("Total price: ₪" + order.getTotalPrice());
        for (Map.Entry<Flower, Integer> entry : order.getItems().entrySet()) {
            System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue());
        }
        System.out.println("Greeting: " + order.getGreetingText());
        System.out.println("Delivery address: " + order.getDeliveryAddress());
        System.out.println("Delivery date: " + order.getDeliveryDate());
        System.out.println("Delivery time: " + order.getDeliveryTime());

        // ✅ Update flower supply
        for (Map.Entry<Flower, Integer> entry : order.getItems().entrySet()) {
            Flower flower = entry.getKey();
            int quantityPurchased = entry.getValue();
            flower.setSupply(flower.getSupply() - quantityPurchased);
        }

// ✅ Add to upcoming deliveries instead of purchase history
        SecondaryController.instance.addOrderToHistory(order);

        // ✅ Clear cart
        cartMap.clear();
        SecondaryController.refreshCartStatic();

        // ✅ Update flower cards in catalog (ADD THIS LINE HERE!)
        SecondaryController.instance.updateFlowerCardsAfterPurchase();

        // ✅ Show popup
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Order placed successfully! Thank you 🌸");
            alert.showAndWait();
            SecondaryController.clearCart();
        });

        try {
            List<Flower> flowersToUpdate = new ArrayList<>(order.getItems().keySet());
            SimpleClient.getClient().sendToServer(new UpdateFlowerSupplyRequest(flowersToUpdate));
        } catch (Exception e) {
            e.printStackTrace();
        }


        Stage stage = (Stage) GreetingField.getScene().getWindow();
        stage.close();
    }

}
