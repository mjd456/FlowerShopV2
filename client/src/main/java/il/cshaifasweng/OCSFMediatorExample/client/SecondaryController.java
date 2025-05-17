package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

import javafx.scene.image.ImageView;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;


public class SecondaryController {

    @FXML
    private VBox FlowerPageVbox;

    @FXML
    private AnchorPane FlowersAnchor;

    @FXML
    private ScrollPane FlowersScrollPane;

    @FXML
    private Tab FlowersTab;

    @FXML
    private AnchorPane MainFrame;

    @FXML
    private TabPane MainTabsFrame;

    @FXML
    private AnchorPane SettingsAnchor;

    @FXML
    private Tab SettingsTab;

    public void addFlowersToVBox(Map<Flower, byte[]> flowerImageMap) {
        FlowerPageVbox.getChildren().clear(); // clear old content

        for (Map.Entry<Flower, byte[]> entry : flowerImageMap.entrySet()) {
            Flower flower = entry.getKey();
            byte[] imageData = entry.getValue();

            // ✅ Main horizontal container
            HBox flowerBox = new HBox(10); // spacing between image and text
            flowerBox.setStyle("-fx-border-color: lightgray; -fx-background-color: white;");
            flowerBox.setPadding(new Insets(10));
            flowerBox.setAlignment(Pos.CENTER_LEFT);
            flowerBox.setPrefHeight(120); // Optional: for consistent height

            // ✅ ImageView
            ImageView imageView = new ImageView();
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                Image image = new Image(bais);
                imageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Failed to load image for: " + flower.getName());
                e.printStackTrace();
            }
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            // ✅ Text + Button column
            VBox textBox = new VBox(5);
            Label name = new Label("Name: " + flower.getName());
            Label price = new Label("Price: $" + flower.getPrice());
            Button buyButton = new Button("Buy");
            buyButton.setOnAction(e -> {
                System.out.println("Buying: " + flower.getName());
            });

            textBox.getChildren().addAll(name, price, buyButton);

            // ✅ Assemble and add
            flowerBox.getChildren().addAll(imageView, textBox);
            FlowerPageVbox.getChildren().add(flowerBox);
        }

        // ✅ Optional: scroll to bottom after layout
        Platform.runLater(() -> FlowersScrollPane.setVvalue(1.0));
    }

    @org.greenrobot.eventbus.Subscribe
    public void onRefreshList(FlowerListEventBus event) {
        javafx.application.Platform.runLater(()-> {
            addFlowersToVBox(event.getFlowerMap());
        });
    }


    @FXML
    void initialize() {
        assert FlowerPageVbox != null : "fx:id=\"FlowerPageVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersAnchor != null : "fx:id=\"FlowersAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersScrollPane != null : "fx:id=\"FlowersScrollPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersTab != null : "fx:id=\"FlowersTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MainFrame != null : "fx:id=\"MainFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MainTabsFrame != null : "fx:id=\"MainTabsFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsAnchor != null : "fx:id=\"SettingsAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsTab != null : "fx:id=\"SettingsTab\" was not injected: check your FXML file 'secondary.fxml'.";
        EventBus.getDefault().register(this);
        FlowersScrollPane.setFitToWidth(true);
        FlowersScrollPane.setFitToHeight(false);
        FlowersScrollPane.setContent(FlowerPageVbox);
        FlowerPageVbox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMinHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMaxHeight(Region.USE_COMPUTED_SIZE);
        System.out.println("[SecondaryController] Initialized");
        App.notifySecondaryReady();
    }

}
