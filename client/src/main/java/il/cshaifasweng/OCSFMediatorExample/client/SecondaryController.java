package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Account;
import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import il.cshaifasweng.OCSFMediatorExample.entities.LogoutRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.UpdateFlowerRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SecondaryController {

    @FXML
    private Button BuyCartButton;

    @FXML
    private AnchorPane CartAnchor;

    @FXML
    private ScrollPane CartScrollPane;

    @FXML
    private Tab CartTab;

    @FXML
    private AnchorPane FeedBackAnchor;

    @FXML
    private TextArea FeedBackDetails;

    @FXML
    private Tab FeedBackTab;

    @FXML
    private TextField FeedBackTitle;

    @FXML
    private VBox FlowerPageVbox;

    @FXML
    private AnchorPane FlowersAnchor;

    @FXML
    private ScrollPane FlowersScrollPane;

    @FXML
    private Tab FlowersTab;

    @FXML
    private Button LogOutButton;

    @FXML
    private AnchorPane MainFrame;

    @FXML
    private TabPane MainTabsFrame;

    @FXML
    private ScrollPane ManagerCatalogSelector;

    @FXML
    private VBox ManagerCatalogSelectorVbox;

    @FXML
    private Tab ManagerPanel;

    @FXML
    private AnchorPane ManagerPanelAnchor;

    @FXML
    private Pane ManagerPanelPane;

    @FXML
    private ScrollPane ManagerPanelScroll;

    @FXML
    private Label ProfileSayHelloLabel;

    @FXML
    private Button PushFeedBack;

    @FXML
    private Button RefreshCatalogBtn;

    @FXML
    private AnchorPane SettingsAnchor;

    @FXML
    private Tab SettingsTab;

    @FXML
    private Button SortCatalogBtn;

    @FXML
    private Label TotalFlowersLabel;

    @FXML
    private Label TotalFlowersNum;

    @FXML
    private Label TotalPriceLabel;

    @FXML
    private Label TotalPriceNum;

    private Tab[] ManagerTabs;

    private Account account;

    private List<Pair<Flower, HBox>> cachedFlowerNodes = new ArrayList<>();

    private final List<Pair<Flower, Integer>> cartList = new ArrayList<>();

    private String Sorted = "Unsorted";

    public void addFlowersToVBox(Map<Flower, byte[]> flowerImageMap) {
        FlowerPageVbox.getChildren().clear();
        cachedFlowerNodes.clear();
        System.out.println("Adding flowers to VBox");

        for (Map.Entry<Flower, byte[]> entry : flowerImageMap.entrySet()) {
            Flower flower = entry.getKey();
            byte[] imageData = entry.getValue();

            HBox flowerBox = new HBox(10);
            flowerBox.setStyle("-fx-border-color: lightgray; -fx-background-color: white;");
            flowerBox.setPadding(new Insets(10));
            flowerBox.setAlignment(Pos.CENTER_LEFT);
            flowerBox.setPrefHeight(140);
            flowerBox.setMaxWidth(Double.MAX_VALUE);

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

            VBox textBox = new VBox(5);
            Label name = new Label("Name: " + flower.getName());
            Label price = new Label("Price: $" + flower.getPrice());
            Label desc = new Label("Description: " + flower.getDescription());
            Label supply = new Label("Supply: " + flower.getSupply());

            Spinner<Integer> quantitySpinner = new Spinner<>(1, flower.getSupply(), 1);
            TextField spinnerEditor = quantitySpinner.getEditor();

            spinnerEditor.setTextFormatter(new TextFormatter<String>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*")) {
                    return change; // Accept
                }
                return null; // Reject
            }));

            quantitySpinner.setEditable(true);
            quantitySpinner.setMaxWidth(80);

            Button addToCartButton = new Button("Add to Cart");
            addToCartButton.setOnAction(e -> {
                int amount = quantitySpinner.getValue();
                cartList.add(new Pair<>(flower, amount));
                System.out.println("Added to cart: " + flower.getName() + " x" + amount);
            });

            textBox.getChildren().addAll(name, price, desc, supply, quantitySpinner, addToCartButton);
            flowerBox.getChildren().addAll(imageView, textBox);

            cachedFlowerNodes.add(new Pair<>(flower, flowerBox));
            FlowerPageVbox.getChildren().add(flowerBox);
        }
        Platform.runLater(() -> {
            FlowersScrollPane.setVvalue(0.0);
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onRefreshList(FlowerListEventBus event) {
        javafx.application.Platform.runLater(() -> {
            Tab selectedTab = MainTabsFrame.getSelectionModel().getSelectedItem();

            if (selectedTab == FlowersTab) {
                addFlowersToVBox(event.getFlowerMap());
            } else if (selectedTab == ManagerPanel) {
                List<Flower> flowerList = new ArrayList<>(event.getFlowerMap().keySet());
                populateManagerCatalog(flowerList);
            } else {
                System.out.println("Flower data received, but not on a tab that uses it.");
            }
        });
    }

    public void sortByPrice() {
        FlowerPageVbox.getChildren().clear();

        if (Sorted.equals("Unsorted") || Sorted.equals("LowToHigh")) {
            // Sort ascending
            cachedFlowerNodes.sort(Comparator.comparing(pair -> pair.getKey().getPrice()));

            for (Pair<Flower, HBox> pair : cachedFlowerNodes) {
                FlowerPageVbox.getChildren().add(pair.getValue());
            }

            Sorted = "HighToLow";
        } else if (Sorted.equals("HighToLow")) {
            // Sort descending
            cachedFlowerNodes.sort(Comparator.comparing((Pair<Flower, HBox> pair) -> pair.getKey().getPrice()).reversed());

            for (Pair<Flower, HBox> pair : cachedFlowerNodes) {
                FlowerPageVbox.getChildren().add(pair.getValue());
            }
            Sorted = "LowToHigh";
        }
        Platform.runLater(() -> FlowersScrollPane.setVvalue(0.0));
    }



    public void setUserRole() {
        String role = account.getAccountLevel();

        if ("Customer".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                for (Tab tab : ManagerTabs) {
                    MainTabsFrame.getTabs().remove(tab);
                }
            });
        } else if ("Manager".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                MainTabsFrame.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                    if (newTab == ManagerPanel) {

                        try {
                            SimpleClient.getClient().sendToServer("RequestFlowerCatalogForManager");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (oldTab == ManagerPanel) {
                        ManagerCatalogSelectorVbox.getChildren().clear();
                    }
                });
            });
        }
    }


    @org.greenrobot.eventbus.Subscribe
    public void onSetAccountLevel(SetAccountLevel event) {
        javafx.application.Platform.runLater(()-> {
            System.out.println("Received sticky event for role: " + event.getAccount().getAccountLevel());
            account = event.getAccount();
            ProfileSayHelloLabel.setText("Hello " + account.getFirstName() + " " + account.getLastName() );
            System.out.println("Account Level: " + account.getAccountLevel());
            setUserRole();
        });
    }

    @FXML
    public void LogOut(ActionEvent event) {
        try {
            StageManager.replaceScene("primary", "Authenticator");
            SimpleClient.getClient().sendToServer(new LogoutRequest(account));
            account = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateManagerCatalog(List<Flower> flowerList) {
        ManagerCatalogSelectorVbox.getChildren().clear();

        for (Flower flower : flowerList) {
            VBox flowerBox = new VBox(5);
            flowerBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f0f0f0;");
            flowerBox.setMaxWidth(Double.MAX_VALUE); // 🔁 makes it expand fully
            VBox.setVgrow(flowerBox, Priority.ALWAYS);

            Label nameLabel = new Label("Name: " + flower.getName());
            Label priceLabel = new Label("Price: $" + flower.getPrice());
            Label descLabel = new Label("Description: " + flower.getDescription());
            descLabel.setWrapText(true);

            Button editBtn = new Button("Edit");
            VBox editPane = new VBox(5);
            editPane.setVisible(false);
            editPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");
            editPane.setMaxWidth(Double.MAX_VALUE); // 👈 important for inner content

            TextField nameField = new TextField(flower.getName());
            TextField priceField = new TextField(String.valueOf(flower.getPrice()));
            TextArea descField = new TextArea(flower.getDescription());
            descField.setPrefHeight(60);
            descField.setWrapText(true);
            descField.setMaxWidth(Double.MAX_VALUE);

            Button saveBtn = new Button("Save");
            Button cancelBtn = new Button("Cancel");

            HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
            buttonBox.setAlignment(Pos.CENTER_LEFT);

            saveBtn.setOnAction(e -> {
                String newName = nameField.getText().trim();
                String newDesc = descField.getText().trim();
                String priceText = priceField.getText().trim();

                boolean valid = true;

                // Validate name
                if (newName.isEmpty()) {
                    nameField.setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    nameField.setStyle(""); // reset style
                }

                // Validate price
                if (!priceText.matches("\\d+(\\.\\d+)?")) {
                    priceField.setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    priceField.setStyle(""); // reset style
                }

                if (!valid) {
                    System.err.println("Validation failed: please fix highlighted fields.");
                    return;
                }

                double newPrice = Double.parseDouble(priceText);

                // Update flower object
                flower.setName(newName);
                flower.setDescription(newDesc);
                flower.setPrice(newPrice);

                // Send to server
                try {
                    SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
                    System.out.println("Flower updated and sent to server.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Failed to send flower update.");
                }

                // Refresh UI
                populateManagerCatalog(flowerList);
            });



            cancelBtn.setOnAction(e -> editPane.setVisible(false));
            editBtn.setOnAction(e -> editPane.setVisible(!editPane.isVisible()));

            editPane.getChildren().addAll(
                    new Label("Edit Name:"), nameField,
                    new Label("Edit Price:"), priceField,
                    new Label("Edit Description:"), descField,
                    buttonBox
            );

            flowerBox.getChildren().addAll(nameLabel, priceLabel, descLabel, editBtn, editPane);
            ManagerCatalogSelectorVbox.getChildren().add(flowerBox);
        }

        // Force VBox to fill the ScrollPane width
        ManagerCatalogSelectorVbox.setFillWidth(true);
    }

    private void updateFlowerOnServer(Flower flower) {
        try {
            SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void RefreshCatalog(ActionEvent event) {
        try {
            SimpleClient.getClient().sendToServer("RefreshList");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SortCatalog(ActionEvent event) {
        sortByPrice();
    }


    @FXML
    void BuyFlowersFromCart(ActionEvent event) {

    }


    @FXML
    void SendFeedBack(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert BuyCartButton != null : "fx:id=\"BuyCartButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartAnchor != null : "fx:id=\"CartAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartScrollPane != null : "fx:id=\"CartScrollPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartTab != null : "fx:id=\"CartTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackAnchor != null : "fx:id=\"FeedBackAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackDetails != null : "fx:id=\"FeedBackDetails\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackTab != null : "fx:id=\"FeedBackTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackTitle != null : "fx:id=\"FeedBackTitle\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowerPageVbox != null : "fx:id=\"FlowerPageVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersAnchor != null : "fx:id=\"FlowersAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersScrollPane != null : "fx:id=\"FlowersScrollPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersTab != null : "fx:id=\"FlowersTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert LogOutButton != null : "fx:id=\"LogOutButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MainFrame != null : "fx:id=\"MainFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MainTabsFrame != null : "fx:id=\"MainTabsFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerCatalogSelector != null : "fx:id=\"ManagerCatalogSelector\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerCatalogSelectorVbox != null : "fx:id=\"ManagerCatalogSelectorVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanel != null : "fx:id=\"ManagerPanel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelAnchor != null : "fx:id=\"ManagerPanelAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelPane != null : "fx:id=\"ManagerPanelPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelScroll != null : "fx:id=\"ManagerPanelScroll\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileSayHelloLabel != null : "fx:id=\"ProfileSayHelloLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PushFeedBack != null : "fx:id=\"PushFeedBack\" was not injected: check your FXML file 'secondary.fxml'.";
        assert RefreshCatalogBtn != null : "fx:id=\"RefreshCatalogBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsAnchor != null : "fx:id=\"SettingsAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsTab != null : "fx:id=\"SettingsTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SortCatalogBtn != null : "fx:id=\"SortCatalogBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert TotalFlowersLabel != null : "fx:id=\"TotalFlowersLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert TotalFlowersNum != null : "fx:id=\"TotalFlowersNum\" was not injected: check your FXML file 'secondary.fxml'.";
        assert TotalPriceLabel != null : "fx:id=\"TotalPriceLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert TotalPriceNum != null : "fx:id=\"TotalPriceNum\" was not injected: check your FXML file 'secondary.fxml'.";

        ManagerTabs = new Tab[] {
                ManagerPanel
        };

        EventBus.getDefault().register(this);
        FlowersScrollPane.setFitToWidth(true);
        FlowersScrollPane.setFitToHeight(false);
        FlowersScrollPane.setContent(FlowerPageVbox);
        FlowerPageVbox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMinHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMaxHeight(Region.USE_COMPUTED_SIZE);

        MainTabsFrame.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == FlowersTab) {
                // Refresh view when user comes back to the tab
                if (cachedFlowerNodes.isEmpty()) {
                    try {
                        SimpleClient.getClient().sendToServer("RefreshList");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    FlowerPageVbox.getChildren().setAll(
                            cachedFlowerNodes.stream()
                                    .map(Pair::getValue) // get the HBox
                                    .collect(Collectors.toList())
                    );
                }
            }
            else if (oldTab == FlowersTab) {
                // Clear UI to save memory
                System.out.println("Removing flowers");
                FlowerPageVbox.getChildren().clear();
                cachedFlowerNodes.clear();
            }
        });

        System.out.println("[SecondaryController] Initialized");
        App.notifySecondaryReady();
    }
}
