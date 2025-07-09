package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import javassist.Loader;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.Subscribe;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SecondaryController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label AccInfoCCNum;

    @FXML
    private Label AccInfoCCV;

    @FXML
    private Label AccInfoCCValidUntil;

    @FXML
    private Label AccInfoEmail;

    @FXML
    private Label AccInfoPassword;

    @FXML
    private Label AccInfoPhoneNum;

    @FXML
    private Button AddNewFlower;

    @FXML
    private Button CancelRenewButton;

    @FXML
    private AnchorPane CartAnchor;

    @FXML
    private Label CartPriceLabel;

    @FXML
    private Tab CartTab;

    @FXML
    private Button CloseBtn;

    @FXML
    private Button ContinueToBuyButton;

    @FXML
    private HBox CustomTitleBar;

    @FXML
    private Tab CustomerServicePanel;

    @FXML
    private AnchorPane FeedBackAnchor;

    @FXML
    private TextArea FeedBackDetails;

    @FXML
    private Label FeedBackLabelText;

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
    private Label FreeUserLabel;

    @FXML
    private Button LogOutButton;

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
    private Button MinimizeBtn;

    @FXML
    private ScrollPane MyFeedBackScrollFrane;

    @FXML
    private Label MyFeedBacksText;

    @FXML
    private VBox MyFeedbacksVbox;

    @FXML
    private Label NewCCError;

    @FXML
    private TextField NewCardCCV;

    @FXML
    private DatePicker NewCardDate;

    @FXML
    private TextField NewCardNumber;

    @FXML
    private TextField NewFlowerColor;

    @FXML
    private TextArea NewFlowerDesc;

    @FXML
    private TextField NewFlowerName;

    @FXML
    private TextField NewFlowerPrice;

    @FXML
    private TextField NewFlowerSupply;

    @FXML
    private Label NewPassError;

    @FXML
    private Label PlusLabelPayment;

    @FXML
    private Button PlusUpgradeButton;

    @FXML
    private Label PlusUserLabel;

    @FXML
    private Label ProfileSayHelloLabel;

    @FXML
    private VBox ProfileTabConfirmPassText;

    @FXML
    private PasswordField ProfileTabNewConfirmPassText;

    @FXML
    private PasswordField ProfileTabNewPassText;

    @FXML
    private ScrollPane PurchaseHistoryScrollFrame;

    @FXML
    private Label PurchaseHistoryText;

    @FXML
    private VBox PurchaseHistoryVbox;

    @FXML
    private Button PushFeedBack;

    @FXML
    private Button ResetMyPasswordButton;

    @FXML
    private VBox ResolvedFeedbackVBOX;

    @FXML
    private AnchorPane SettingsAnchor;

    @FXML
    private Pane SettingsPane;

    @FXML
    private Tab SettingsTab;

    @FXML
    private Button SortCatalogBtn;

    @FXML
    private Label SubscribtionLevelLabel;

    @FXML
    private VBox UnresolvedFeedbackVBOX;

    @FXML
    private Button UpdateNewCC;

    @FXML
    private VBox CartVBox;

    @FXML
    private Label UpgradingAccountError;

    @FXML
    private Label DiscountLabel;


    //==================CustomHeader=====================//

    private double xOffset = 0, yOffset = 0;

    private Tab[] ManagerTabs;

    private Account account;

    private boolean Guest;

    private List<Pair<Flower, HBox>> cachedFlowerNodes = new ArrayList<>();

    private final Map<Flower, Integer> cartMap = new HashMap<>();

    private String Sorted = "Unsorted";

    private List<OrderSQL> purchaseHistoryList = new ArrayList<>();

    public static SecondaryController instance;
    public Map<Flower, Integer> getCartMap() {
        return cartMap;
    }

    public void addOrderToHistory(OrderSQL order) {
        purchaseHistoryList.add(order);
        updatePurchaseHistoryUI();
    }

    @org.greenrobot.eventbus.Subscribe
    public void onOrderSavedConfirmation(OrderSavedConfirmation event) {
        Platform.runLater(() -> {
            if (account != null) {
                try {
                    SimpleClient.getClient().sendToServer(new GetUserOrdersRequest(account.getId()));
                    System.out.println("Requested updated orders after server confirmed save");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Map<Long, Label> orderErrorLabels = new HashMap<>(); // orderId -> errorLabel
    private Map<Long, Button> orderCancelButtons = new HashMap<>();

    public void updatePurchaseHistoryUI() {
        Platform.runLater(() -> {
            PurchaseHistoryVbox.getChildren().clear();
            orderErrorLabels.clear(); // reset when reloading UI

            for (OrderSQL order : purchaseHistoryList) {
                VBox orderBox = new VBox(5);
                orderBox.setStyle("-fx-padding: 10; -fx-border-color: #4D8DFF; -fx-border-radius: 8; -fx-background-radius: 8;");

                Label detailsLabel = new Label("Details: " + order.getDetails());
                Label priceLabel = new Label("Price: ₪" + order.getTotalPrice());
                Label statusLabel = new Label("Status: " + order.getStatus());
                Label dateLabel = new Label("Date: " + order.getDeliveryDate());
                Label timeLabel = new Label("Time: " + order.getDeliveryTime());
                Label errorLabel = new Label();
                errorLabel.setVisible(false);
                orderErrorLabels.put((long)order.getId(), errorLabel);

                // Only add Cancel button if not canceled already
                boolean canCancel = true;
                if ("canceled".equalsIgnoreCase(order.getStatus())) {
                    canCancel = false;
                } else {
                    try {
                        LocalDate orderDate;
                        if (order.getDeliveryDate() instanceof java.sql.Date) {
                            orderDate = ((java.sql.Date) order.getDeliveryDate()).toLocalDate();
                        } else {
                            orderDate = order.getDeliveryDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                        }

                        java.time.LocalTime orderTime = java.time.LocalTime.parse(order.getDeliveryTime());
                        java.time.LocalDateTime orderDateTime = java.time.LocalDateTime.of(orderDate, orderTime);

                        if (java.time.LocalDateTime.now().isAfter(orderDateTime)) {
                            canCancel = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        canCancel = false;
                    }
                }

                if (canCancel) {
                    Button cancelButton = new Button("Cancel");
                    cancelButton.setDisable(!canCancel);
                    orderCancelButtons.put((long)order.getId(), cancelButton);

                    cancelButton.setOnAction(e -> {
                        try {
                            SimpleClient.getClient().sendToServer(new CancelOrderRequest(order.getId()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    orderBox.getChildren().addAll(detailsLabel, priceLabel, statusLabel, dateLabel, timeLabel, errorLabel, cancelButton);
                } else {
                    orderBox.getChildren().addAll(detailsLabel, priceLabel, statusLabel, dateLabel, timeLabel, errorLabel);
                }

                PurchaseHistoryVbox.getChildren().add(orderBox);
            }
        });
    }




    public void addFlowersToVBox(Map<Flower, byte[]> flowerImageMap) {
        FlowerPageVbox.getChildren().clear();
        cachedFlowerNodes.clear();
        System.out.println("Adding flowers to VBox");

        for (Map.Entry<Flower, byte[]> entry : flowerImageMap.entrySet()) {
            Flower flower = entry.getKey();
            byte[] imageData = entry.getValue();



            HBox flowerBox = new HBox(10);
            flowerBox.setStyle("-fx-border-color: #244060;");
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

            System.out.println("Processing: " + (flower == null ? "null" : flower.getName()));
            System.out.println("Image data null? " + (imageData == null));

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            VBox textBox = new VBox(5);
            Label name = new Label("Name: " + flower.getName());
            Label price = new Label("Price: ₪" + flower.getPrice());
            Label color = new Label("Color: " + flower.getColor());
            Label desc = new Label("Description: " + flower.getDescription());
            Label supply = new Label("Supply: " + flower.getSupply());

            if (flower.getSupply() <= 0) {
                Label outOfStock = new Label("Out of Stock");
                outOfStock.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                textBox.getChildren().addAll(name, price, color, desc, supply, outOfStock);
            } else {
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

                if (!Guest) {
                    Button addToCartButton = new Button("Add to Cart");
                    addToCartButton.setOnAction(e -> {
                        int amount = quantitySpinner.getValue();
                        int currentQty = cartMap.getOrDefault(flower, 0);
                        int newTotal = currentQty + amount;

                        if (newTotal <= flower.getSupply()) {
                            cartMap.put(flower, newTotal);
                            System.out.println("Added to cart: " + flower.getName() + " x" + amount);
                            showCart();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Quantity Limit");
                            alert.setHeaderText(null);
                            alert.setContentText("Cannot add more than available supply (" + flower.getSupply() + ").");

                            DialogPane dialogPane = alert.getDialogPane();
                            URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
                            if (cssUrl != null) {
                                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
                            }
                            alert.showAndWait();
                        }
                    });
                    textBox.getChildren().addAll(name, price, color, desc, supply, quantitySpinner, addToCartButton);
                } else {
                    textBox.getChildren().addAll(name, price, color, desc, supply, quantitySpinner);
                }
            }

            System.out.println("Adding node for: " + flower.getName());

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
        String role = account == null? "Guest":account.getAccountLevel();
        if ("Guest".equalsIgnoreCase(role)){
            Platform.runLater(() -> {
                List<Tab> toRemove = new ArrayList<>();
                for (Tab tab : MainTabsFrame.getTabs()) {
                    String text = tab.getText();
                    if (!"Catalog".equals(text) && !"Profile/Settings".equals(text)) {
                        toRemove.add(tab);
                    }
                }
                ResetMyPasswordButton.setVisible(false);
                PurchaseHistoryScrollFrame.setVisible(false);
                PurchaseHistoryText.setVisible(false);
                MyFeedBacksText.setVisible(false);
                MyFeedBackScrollFrane.setVisible(false);
                MainTabsFrame.getTabs().removeAll(toRemove);

                List<javafx.scene.Node> toRemoveNodes = new ArrayList<>();
                for (javafx.scene.Node node : SettingsPane.getChildren()) {
                    if (node != LogOutButton && node != ProfileSayHelloLabel) {
                        toRemoveNodes.add(node);
                    }
                }
                SettingsPane.getChildren().removeAll(toRemoveNodes);
                SettingsPane.setPrefWidth(585);
                SettingsPane.setPrefHeight(300);

            });
        }
        else if ("Customer".equalsIgnoreCase(role)) {
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
        } else if ("Customer service".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                for (Tab tab : ManagerTabs) {
                    if(tab != CustomerServicePanel){
                        MainTabsFrame.getTabs().remove(tab);
                    }
                }
            });
        }
    }

    @org.greenrobot.eventbus.Subscribe
    public void onGetUserOrdersResponse(GetUserOrdersResponse event) {
        Platform.runLater(() -> {
            purchaseHistoryList.clear();
            purchaseHistoryList.addAll(event.getOrders());
            updatePurchaseHistoryUI();
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onSetAccountLevel(SetAccountLevel event) {
        javafx.application.Platform.runLater(()-> {
            account = event.getAccount();
            Guest = account == null;
            if(!Guest) {
                System.out.println("Received sticky event for role: " + event.getAccount().getAccountLevel());
                ProfileSayHelloLabel.setText("Hello " + account.getFirstName() + " " + account.getLastName());
                System.out.println("Account Level: " + account.getAccountLevel() + ", user : " + account.getSubscribtion_level());
                SubscribtionLevelLabel.setText(account.getSubscribtion_level() + " user");
                if(account.getSubscribtion_level().equals("Free")){
                    FreeUserLabel.setVisible(true);
                    CancelRenewButton.setVisible(false);
                    PlusUserLabel.setVisible(false);
                }else{
                    FreeUserLabel.setVisible(false);
                    PlusUpgradeButton.setVisible(false);
                    if(account.getAuto_renew_subscription().equals("Yes")){
                        CancelRenewButton.setVisible(true);
                        PlusUserLabel.setText("Renew at " + account.getSubscription_expires_at());
                    }else {
                        CancelRenewButton.setVisible(false);
                        PlusUserLabel.setText("Expires at " + account.getSubscription_expires_at());
                    }
                }
                try {
                    SimpleClient.getClient().sendToServer(new GetUserOrdersRequest(account.getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("Received sticky event for role: guest");
                ProfileSayHelloLabel.setText("Hello guest");
                System.out.println("Account Level: guest , user : guest");
                SubscribtionLevelLabel.setText("Guest user");
                CancelRenewButton.setVisible(false);
                PlusUserLabel.setVisible(false);
                PlusUpgradeButton.setVisible(false);
            }


            setUserRole();
        });
    }

    @FXML
    public void LogOut(ActionEvent event) {
        try {
            StageManager.replaceScene("primary", "Authenticator");
            if(!Guest) {
                SimpleClient.getClient().sendToServer(new LogoutRequest(account));
            }
            account = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        ((Stage) CustomTitleBar.getScene().getWindow()).close();
        if(!Guest) {
            try {
                SimpleClient.getClient().sendToServer(new LogoutRequest(account));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        account = null;
    }

    @FXML
    private void minimizeWindow() {
        ((Stage) CustomTitleBar.getScene().getWindow()).setIconified(true);
    }

    public void populateManagerCatalog(List<Flower> flowerList) {
        ManagerCatalogSelectorVbox.getChildren().clear();
        ManagerCatalogSelector.setFitToWidth(true);

        for (Flower flower : flowerList) {
            // Main Pane for the flower, vertical orientation
            VBox flowerBox = new VBox(8);
            flowerBox.setStyle("-fx-border-color: #4D8DFF; -fx-padding: 14 16 14 16; -fx-background-radius: 8;");
            flowerBox.setMaxWidth(430);

            // Name, Price, and Description (Label with wrap)
            Label nameLabel = new Label("Name: " + flower.getName());
            Label priceLabel = new Label("Price: ₪" + flower.getPrice());
            Label colorLabel = new Label("Color: " + flower.getColor());           // <---- ADD THIS
            Label supplyLabel = new Label("Supply: " + flower.getSupply());        // <---- AND THIS
            Label descLabel = new Label("Description: " + flower.getDescription());
            descLabel.setWrapText(true);

            Button editBtn = new Button("Edit");
            VBox editPane = new VBox(10);
            editPane.setStyle("-fx-background-color: #242b3b; -fx-padding: 12; -fx-background-radius: 0 0 8 8;");
            editPane.setMaxWidth(Double.MAX_VALUE);
            editPane.setVisible(false);
            editPane.setManaged(false); // So the VBox height is correct when not expanded

            Button deleteBtn = new Button("Delete");

            // Input fields
            TextField nameField = new TextField(flower.getName());
            TextField colorField = new TextField(flower.getColor());
            TextField priceField = new TextField(String.valueOf(flower.getPrice()));
            TextField supplyField = new TextField(String.valueOf(flower.getSupply()));
            TextArea descField = new TextArea(flower.getDescription());
            descField.setPrefHeight(80);
            descField.setWrapText(true);
            descField.setMaxWidth(Double.MAX_VALUE);

            Button saveBtn = new Button("Save");
            Button cancelBtn = new Button("Cancel");
            HBox buttonBox = new HBox(8, saveBtn, cancelBtn);

            // --- Input validation on Save ---
            saveBtn.setOnAction(e -> {
                String newName = nameField.getText().trim();
                String newDesc = descField.getText().trim();
                String priceText = priceField.getText().trim();
                String supplyText = supplyField.getText().trim();
                String newColor = colorField.getText().trim();
                int newSupply = -1;
                boolean valid = true;

                try {
                    newSupply = Integer.parseInt(supplyText);
                    if (newSupply < 0) throw new NumberFormatException();
                    supplyField.setStyle("");
                } catch (NumberFormatException ex) {
                    supplyField.setStyle("-fx-border-color: red;");
                    valid = false;
                }

                if (newName.isEmpty() || !newName.matches("^[A-Za-z\\s]+$")) {
                    nameField.setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    nameField.setStyle("");
                }

                if (newColor.isEmpty() || !newColor.matches("^[A-Za-z\\s]+$")) {
                    colorField.setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    colorField.setStyle("");
                }

                // Price: only digits, at most one dot, no leading dot
                if (!priceText.matches("^\\d+(\\.\\d{1,2})?$")) {
                    priceField.setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    priceField.setStyle("");
                }

                if (!valid) {
                    System.err.println("Validation failed: please fix highlighted fields.");
                    return;
                }

                double newPrice = Double.parseDouble(priceText);

                // Update and send
                flower.setName(newName);
                flower.setDescription(newDesc);
                flower.setPrice(newPrice);
                flower.setColor(newColor);
                flower.setSupply(newSupply);

                try {
                    SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
                    System.out.println("Flower updated and sent to server.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                populateManagerCatalog(flowerList);
            });

            cancelBtn.setOnAction(e -> {
                editPane.setVisible(false);
                editPane.setManaged(false);
            });

            deleteBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("Are you sure you want to delete this flower?\n\n" + "Flower name : "+  flower.getName());

                DialogPane dialogPane = alert.getDialogPane();
                URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
                if (cssUrl != null) {
                    dialogPane.getStylesheets().add(cssUrl.toExternalForm());
                } else {
                    System.err.println("Could not find dark-theme.css!");
                }


                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Send delete request to server
                    try {
                        SimpleClient.getClient().sendToServer(new DeleteFlowerRequest(flower.getId()));
                        System.out.println("Sent delete request for flower: " + flower.getName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            // --- Show/Hide edit pane with animation (optional) ---
            editBtn.setOnAction(e -> {
                boolean nowVisible = !editPane.isVisible();
                editPane.setVisible(nowVisible);
                editPane.setManaged(nowVisible);
                if (nowVisible) {
                    // Optionally focus first input field
                    nameField.requestFocus();
                }
            });

            // Layout for edit pane
            editPane.getChildren().setAll(
                    new Label("Edit Name:"), nameField,
                    new Label("Edit Price:"), priceField,
                    new Label("Edit Supply:"), supplyField,
                    new Label("Edit Color:"), colorField,
                    new Label("Edit Description:"), descField,
                    buttonBox
            );

            // Add components to the main flower box
            HBox actionsBox = new HBox(8, editBtn, deleteBtn);
            flowerBox.getChildren().addAll(
                    nameLabel,
                    priceLabel,
                    colorLabel,
                    supplyLabel,
                    descLabel,
                    actionsBox,
                    editPane
            );
            // Add to VBox
            ManagerCatalogSelectorVbox.getChildren().add(flowerBox);
        }
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
    void SortCatalog(ActionEvent event) {
        sortByPrice();
    }

    @FXML
    void ResetMyPassword(ActionEvent event) {
        String newPass = ProfileTabNewPassText.getText();
        String confirmPass = ProfileTabNewConfirmPassText.getText();

        NewPassError.setVisible(false);

        if (!newPass.equals(confirmPass)) {
            NewPassError.setText("Passwords do not match.");
            NewPassError.setVisible(true);
            return;
        }

        try {
            SimpleClient.getClient().sendToServer(
                    new UpdatePasswordRequest(newPass)
            );
            System.out.println("Requested password update");
        } catch (Exception e) {
            e.printStackTrace();
            NewPassError.setText("Failed to send password update.");
            NewPassError.setVisible(true);
        }
    }


    @FXML
    void ContinueToBuy(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/order_details.fxml"));
            VBox pane = loader.load();

            OrderDetailsController controller = loader.getController();
            controller.setOrderData(cartMap,
                    Double.parseDouble(CartPriceLabel.getText().replace("₪", "")),
                    account);

            Stage stage = new Stage();
            stage.setTitle("Order Details");
            stage.setScene(new javafx.scene.Scene(pane));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void UpgradeUserToPlus(ActionEvent event) {
        if (
                account.getCreditCardNumber() != null && !account.getCreditCardNumber().isEmpty() &&
                        account.getCvv() != null && !account.getCvv().isEmpty() &&
                        account.getCreditCardValidUntil() != null &&
                        isCardStillValid(account)
        ) {
            try {
                SimpleClient.getClient().sendToServer(new SubscriptionRequest(account));
                UpgradingAccountError.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else {
            System.err.println("Something went wrong. Please try again.");
            System.out.println("CC: " + account.getCreditCardNumber());
            System.out.println("CVV: " + account.getCvv());
            System.out.println("ValidUntil: " + account.getCreditCardValidUntil());
            System.out.println("isCardStillValid: " + isCardStillValid(account));
            if(isCardStillValid(account) == false) {
                UpgradingAccountError.setText("Error: Card is not valid.");
                UpgradingAccountError.setVisible(true);
            }
        }
    }

    @FXML
    void UpdateNewCCFunction(ActionEvent event) {
        String ccNumber = NewCardNumber.getText().trim();
        String ccv = NewCardCCV.getText().trim();
        LocalDate date = NewCardDate.getValue();

        boolean valid = true;

        // Validate CCV: must be exactly 3 digits
        if (!ccv.matches("\\d{3}")) {
            NewCardCCV.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewCardCCV.setStyle("");
        }

        // Validate card number (simple: 13–19 digits is a common standard)
        if (!ccNumber.matches("\\d{13,19}")) {
            NewCardNumber.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewCardNumber.setStyle("");
        }

        // Validate date: must be selected and after today
        if (date == null || !date.isAfter(LocalDate.now())) {
            NewCardDate.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewCardDate.setStyle("");
        }

        if (!valid) {
            System.err.println("Validation failed: Fix highlighted fields.");
            return;
        }

        // If valid, send update request
        try {
            // Convert LocalDate to java.util.Date
            java.util.Date cardDate = java.sql.Date.valueOf(date);

            // Update the local account object
            account.setCreditCardNumber(ccNumber);
            account.setCvv(ccv);
            account.setCreditCardValidUntil(cardDate);

            // Send to server (pass cardDate instead of date)
            SimpleClient.getClient().sendToServer(
                    new UpdateCreditCardRequest(account.getId(), ccNumber, ccv, cardDate)
            );
            System.out.println("Credit card update requested.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public boolean isCardStillValid(Account account) {
        Date validUntil = account.getCreditCardValidUntil();
        if (validUntil == null) return false;

        // java.sql.Date has .toLocalDate()
        LocalDate cardDate;
        if (validUntil instanceof java.sql.Date) {
            cardDate = ((java.sql.Date) validUntil).toLocalDate();
        } else {
            // fallback if it's java.util.Date (not likely, but for safety)
            cardDate = validUntil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate today = LocalDate.now();
        return !cardDate.isBefore(today); // true if today or after
    }

    @FXML
    void CancelAutoRenewSub(ActionEvent event) {
        if(!account.getSubscribtion_level().equals("Free") && account.getAuto_renew_subscription().equals("Yes")){
            try {
                SimpleClient.getClient().sendToServer(new CancelAutoRenewRequest(account));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @org.greenrobot.eventbus.Subscribe
    public void onFeedBackSuccess(FeedBackSuccess event) {
        javafx.application.Platform.runLater(() -> {
            FeedBackLabelText.setTextFill(javafx.scene.paint.Color.GREEN);
            FeedBackLabelText.setText("Your feedback has been received.");
            FeedBackLabelText.setVisible(true);
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onAllFeedBackInfo(AllFeedBackInfo event) {
        javafx.application.Platform.runLater(() -> {
            ResolvedFeedbackVBOX.getChildren().clear();
            UnresolvedFeedbackVBOX.getChildren().clear();

            for (FeedBackSQL feedback : event.getResponse().getFeedbacks()) {
                VBox feedbackBox = new VBox();
                feedbackBox.setSpacing(5);
                feedbackBox.setStyle(
                        "-fx-padding: 10;" +
                                "-fx-border-color: #4D8DFF;" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;"
                );
                feedbackBox.setMinHeight(Region.USE_PREF_SIZE);
                feedbackBox.setFocusTraversable(false);

                Label titleLabel = new Label("Title: " + feedback.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Label descLabel = new Label("Details: " + feedback.getDetails());
                descLabel.setWrapText(true);

                Label emailLabel = new Label("From: " + feedback.getAccount().getEmail());
                emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2196F3;");

                Label sentLabel = new Label("Sent: " + feedback.getSubmittedAt());
                sentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");

                feedbackBox.getChildren().addAll(titleLabel, descLabel, emailLabel, sentLabel);

                if (feedback.getStatus() == FeedBackSQL.FeedbackStatus.Pending) {
                    HBox buttons = new HBox();
                    buttons.setSpacing(10);

                    Button rejectBtn = new Button("Reject");
                    rejectBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
                    Button doneBtn = new Button("Done");
                    doneBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

                    // Add your button logic here, e.g.:
                    rejectBtn.setOnAction(e -> markFeedbackRejected(feedback));
                    doneBtn.setOnAction(e -> markFeedbackResolved(feedback));

                    buttons.getChildren().addAll(rejectBtn, doneBtn);
                    feedbackBox.getChildren().add(buttons);

                    UnresolvedFeedbackVBOX.getChildren().add(feedbackBox);
                } else {
                    String statusText = (feedback.getStatus() == FeedBackSQL.FeedbackStatus.Resolved) ? "Resolved" : "Rejected";
                    String statusColor = (feedback.getStatus() == FeedBackSQL.FeedbackStatus.Resolved) ? "#4CAF50" : "#F44336";
                    Label statusLabel = new Label("Status: " + statusText);
                    statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

                    Label resolvedLabel = null;
                    if (feedback.getResolvedAt() != null) {
                        resolvedLabel = new Label("At: " + feedback.getResolvedAt());
                        resolvedLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
                    }

                    feedbackBox.getChildren().add(statusLabel);
                    if (resolvedLabel != null) feedbackBox.getChildren().add(resolvedLabel);

                    ResolvedFeedbackVBOX.getChildren().add(feedbackBox);
                }
            }
        });
    }

    // Mark feedback as rejected and notify the server
    private void markFeedbackRejected(FeedBackSQL feedback) {
        try {
            // You may want to show a confirmation dialog here!
            feedback.setStatus(FeedBackSQL.FeedbackStatus.Rejected);
            feedback.setResolvedAt(java.time.LocalDateTime.now());
            // Send update to server
            SimpleClient.getClient().sendToServer(new UpdateFeedbackStatusRequest(feedback.getFeedback_id(), FeedBackSQL.FeedbackStatus.Rejected));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mark feedback as resolved and notify the server
    private void markFeedbackResolved(FeedBackSQL feedback) {
        try {
            feedback.setStatus(FeedBackSQL.FeedbackStatus.Resolved);
            feedback.setResolvedAt(java.time.LocalDateTime.now());
            // Send update to server
            SimpleClient.getClient().sendToServer(new UpdateFeedbackStatusRequest(feedback.getFeedback_id(), FeedBackSQL.FeedbackStatus.Resolved));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SendFeedBack(ActionEvent event) {
        boolean valid = true;

        // Reset styles
        FeedBackTitle.setStyle("");
        FeedBackDetails.setStyle("");

        String title = FeedBackTitle.getText().trim();
        String details = FeedBackDetails.getText().trim();

        // Validation
        if (title.isEmpty()) {
            FeedBackTitle.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (details.isEmpty()) {
            FeedBackDetails.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (!valid) {
            FeedBackLabelText.setTextFill(Color.RED);
            FeedBackLabelText.setText("Please fill all fields correctly.");
            FeedBackLabelText.setVisible(true);
            return;
        }

        Feedback feedback = new Feedback(account, title, details);

        try {
            SimpleClient.getClient().sendToServer(feedback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FeedBackLabelText.setVisible(false);

        // Clear inputs after sending
        FeedBackTitle.clear();
        FeedBackDetails.clear();

    }

    @FXML
    void CustomerServiceGatherInfo(Event event) {
        try {
            SimpleClient.getClient().sendToServer("Send all Feedbacks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void GetProfileInformation(Event event) {
        if(!Guest){
            try {
                SimpleClient.getClient().sendToServer(new GetUserFeedbacksRequest(account.getId()));
                SimpleClient.getClient().sendToServer(new GetUserOrdersRequest(account.getId()));
                AccInfoPhoneNum.setText("Phone Number: " + account.getPhoneNumber());
                AccInfoEmail.setText("Email: " + account.getEmail());
                AccInfoPassword.setText("Password: " + account.getPassword());
                AccInfoCCNum.setText("Credit card number : " + account.getCreditCardNumber());
                AccInfoCCV.setText("CCV : " + account.getCvv());
                AccInfoCCValidUntil.setText("CC valid until : " + account.getCreditCardValidUntil().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            AccInfoPhoneNum.setText("");
            AccInfoEmail.setText("");
            AccInfoPassword.setText("");
            AccInfoCCNum.setText("");
            AccInfoCCV.setText("");
            AccInfoCCValidUntil.setText("");
        }
    }

    @Subscribe
    public void onProfileFeedBacks(ProfileFeedBacks event) {
        GetUserFeedbacksResponse response = event.getResponse();
        Platform.runLater(() -> {
            MyFeedbacksVbox.getChildren().clear();
            for (FeedBackSQL feedback : response.getFeedbacks()) {
                VBox feedbackBox = new VBox();
                feedbackBox.setFocusTraversable(false);
                feedbackBox.setSpacing(4);
                feedbackBox.setStyle(
                        "-fx-padding: 10;" +
                                "-fx-border-color: #4D8DFF;" + // Gray border, not blue
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;" +
                                "-fx-effect: dropshadow(two-pass-box, rgba(33,150,243,0.05), 5, 0, 0, 2);"
                );
                feedbackBox.setMinHeight(Region.USE_PREF_SIZE);

                Label titleLabel = new Label("Title: " + feedback.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                titleLabel.setWrapText(true);
                titleLabel.setFocusTraversable(false);

                Label detailsLabel = new Label("Details: " + feedback.getDetails());
                detailsLabel.setWrapText(true); // Ensures multi-line
                detailsLabel.setStyle("-fx-font-size: 13;");
                detailsLabel.setFocusTraversable(false);

                Label sentLabel = new Label("Sent: " + feedback.getSubmittedAt());
                sentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
                sentLabel.setFocusTraversable(false);

                Label statusLabel = new Label("Status: " + feedback.getStatus());
                statusLabel.setStyle("-fx-font-size: 12; -fx-font-style: italic;");
                statusLabel.setFocusTraversable(false);

                feedbackBox.getChildren().addAll(titleLabel, detailsLabel, sentLabel, statusLabel);

                if (feedback.getStatus() == FeedBackSQL.FeedbackStatus.Resolved && feedback.getResolvedAt() != null) {
                    Label resolvedLabel = new Label("Resolved at: " + feedback.getResolvedAt());
                    resolvedLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #4CAF50;");
                    resolvedLabel.setFocusTraversable(false);
                    feedbackBox.getChildren().add(resolvedLabel);
                }

                MyFeedbacksVbox.getChildren().add(feedbackBox);
            }
        });
    }

    @Subscribe
    public void onNewFeedBack(NewFeedBack event) {
        Platform.runLater(() -> {
            FeedBackSQL feedback = event.getNotification().getFeedback();

            VBox feedbackBox = new VBox();
            feedbackBox.setSpacing(5);
            feedbackBox.setStyle(
                    "-fx-padding: 10;" +
                            "-fx-border-color: #00e2ff;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-color: #2b373e;" +
                            "-fx-background-radius: 8;"
            );
            feedbackBox.setMinHeight(Region.USE_PREF_SIZE);
            feedbackBox.setFocusTraversable(false);

            Label titleLabel = new Label("Title: " + feedback.getTitle() + "  | New");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            Label detailsLabel = new Label("Details: " + feedback.getDetails());
            detailsLabel.setWrapText(true);

            Label emailLabel = new Label("From: " + feedback.getAccount().getEmail());
            emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2196F3;");

            Label sentLabel = new Label("Sent: " + feedback.getSubmittedAt());
            sentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");

            HBox buttons = new HBox();
            buttons.setSpacing(10);

            Button rejectBtn = new Button("Reject");
            rejectBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            Button doneBtn = new Button("Done");
            doneBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

            rejectBtn.setOnAction(e -> markFeedbackRejected(feedback));
            doneBtn.setOnAction(e -> markFeedbackResolved(feedback));
            buttons.getChildren().addAll(rejectBtn, doneBtn);

            feedbackBox.getChildren().addAll(titleLabel, detailsLabel, emailLabel, sentLabel, buttons);

            UnresolvedFeedbackVBOX.getChildren().add(feedbackBox);
        });
    }

    public void updateFlowerCardInVBox(Flower updatedFlower) {
        for (int i = 0; i < cachedFlowerNodes.size(); i++) {
            Pair<Flower, HBox> pair = cachedFlowerNodes.get(i);
            Flower flower = pair.getKey();
            if (flower.getId() == updatedFlower.getId()) {
                HBox flowerBox = pair.getValue();

                VBox textBox = (VBox) flowerBox.getChildren().get(1);

                Label nameLabel = null;
                Label priceLabel = null;
                Label descLabel = null;
                Label supplyLabel = null;
                Spinner<Integer> quantitySpinner = null;

                for (Node node : textBox.getChildren()) {
                    if (node instanceof Label) {
                        Label lbl = (Label) node;
                        String text = lbl.getText();
                        if (text.startsWith("Name: ")) nameLabel = lbl;
                        else if (text.startsWith("Price: ")) priceLabel = lbl;
                        else if (text.startsWith("Description: ")) descLabel = lbl;
                        else if (text.startsWith("Supply: ")) supplyLabel = lbl;
                    } else if (node instanceof Spinner) {
                        quantitySpinner = (Spinner<Integer>) node;
                    }
                }

                if (nameLabel == null || priceLabel == null || descLabel == null || supplyLabel == null || quantitySpinner == null) {
                    System.err.println("Could not find all fields in textBox for flower " + updatedFlower.getName());
                    return;
                }

                // Now you can safely update them
                nameLabel.setText("Name: " + updatedFlower.getName());
                priceLabel.setText("Price: ₪" + updatedFlower.getPrice());
                descLabel.setText("Description: " + updatedFlower.getDescription());
                supplyLabel.setText("Supply: " + updatedFlower.getSupply());
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, updatedFlower.getSupply(), 1
                ));


                cachedFlowerNodes.set(i, new Pair<>(updatedFlower, flowerBox));
                break;
            }
        }
    }

    @org.greenrobot.eventbus.Subscribe
    public void onUpdatedFlowerNotif(updatedFlowerNotif event) {
        javafx.application.Platform.runLater(() -> {
            updateFlowerCardInVBox(event.getUpdatedFlower());
            System.out.println("In secondary controller");
        });
    }



    @org.greenrobot.eventbus.Subscribe
    public void onAccountUpgrade(AccountUpgrade event) {
        javafx.application.Platform.runLater(() -> {
            account = event.getAccount();
            if(account.getSubscribtion_level().equals("Plus")){
                CancelRenewButton.setVisible(true);
                PlusUserLabel.setVisible(true);
                PlusUserLabel.setText("Expires at " + account.getSubscription_expires_at());
                PlusUpgradeButton.setVisible(false);
                FreeUserLabel.setVisible(false);
                SubscribtionLevelLabel.setText("Plus user");
            }
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onAutoRenewResponse(AutoRenewResponse event) {
        javafx.application.Platform.runLater(() -> {
            account = event.getAccount();
            if(account.getAuto_renew_subscription().equals("No")){
                PlusUserLabel.setText("Expires at " + account.getSubscription_expires_at());
                CancelRenewButton.setVisible(false);
            }
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onAccountUpdate(AccountUpdate event) {
        javafx.application.Platform.runLater(() -> {
            account = event.getAccount();
            System.out.println("Updating");
            UpdateAccountSubscriptionStatus();
        });
    }

    @org.greenrobot.eventbus.Subscribe
    public void onUpdateDeletedFlower(UpdateDeletedFlower event) {
        javafx.application.Platform.runLater(() -> {
            int flowerID = event.getFlowerID();

            // Remove from cachedFlowerNodes
            cachedFlowerNodes.removeIf(pair -> pair.getKey().getId() == flowerID);

            // Remove from cartList
            cartMap.entrySet().removeIf(entry -> entry.getKey().getId() == flowerID);
            try {
                SimpleClient.getClient().sendToServer("RequestFlowerCatalogForManager");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void UpdateAccountSubscriptionStatus() {
        SubscribtionLevelLabel.setText(account.getSubscribtion_level() + "user");
        if(account.getSubscribtion_level().equals("Free")){
            FreeUserLabel.setVisible(true);
            PlusUpgradeButton.setVisible(true);
            CancelRenewButton.setVisible(false);
            PlusUserLabel.setVisible(false);
        }else{
            PlusUserLabel.setVisible(true);
            PlusUserLabel.setText((account.getAuto_renew_subscription().equals("Yes")?"Renew at ":"Expires at") + account.getSubscription_expires_at());
            CancelRenewButton.setVisible(true);
            FreeUserLabel.setVisible(false);
            PlusUpgradeButton.setVisible(false);
        }
    }


    @org.greenrobot.eventbus.Subscribe
    public void onUpdatePasswordResponse(UpdatePasswordResponse event) {
        javafx.application.Platform.runLater(() -> {
            NewPassError.setText(event.getMessage());
            NewPassError.setVisible(true);

            if (event.isSuccess()) {
                ProfileTabNewPassText.clear();
                ProfileTabNewConfirmPassText.clear();
                AccInfoPassword.setText("Password : " + account.getPassword());
            }
        });
    }

    @FXML
    void RequestToAddNewFlower(ActionEvent event) {
        String name = NewFlowerName.getText().trim();
        String color = NewFlowerColor.getText().trim();
        String priceText = NewFlowerPrice.getText().trim();
        String supplyText = NewFlowerSupply.getText().trim();
        String desc = NewFlowerDesc.getText().trim();
        String imageId = "";

        boolean valid = true;

        if (name.isEmpty() || !name.matches("^[A-Za-z\\s]+$")) {
            NewFlowerName.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewFlowerName.setStyle("");
        }

        if (color.isEmpty() || !color.matches("^[A-Za-z\\s]+$")) {
            NewFlowerColor.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewFlowerColor.setStyle("");
        }

        double price = -1;
        int supply = -1;

        try {
            price = Double.parseDouble(priceText);
            NewFlowerPrice.setStyle("");
        } catch (NumberFormatException e) {
            NewFlowerPrice.setStyle("-fx-border-color: red;");
            valid = false;
        }

        try {
            supply = Integer.parseInt(supplyText);
            if (supply < 0) throw new NumberFormatException();
            NewFlowerSupply.setStyle("");
        } catch (NumberFormatException e) {
            NewFlowerSupply.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (desc.isEmpty()) {
            NewFlowerDesc.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            NewFlowerDesc.setStyle("");
        }

        if (!valid) {
            System.err.println("Validation failed.");
            return;
        }

        Flower newFlower = new Flower(
                name,
                color,
                desc,
                imageId,
                price,
                supply
        );

        try {
            SimpleClient.getClient().sendToServer(new AddFlowerRequest(newFlower));
            System.out.println("Requested to add new flower: " + name);

            NewFlowerName.setText("");
            NewFlowerColor.setText("");
            NewFlowerPrice.setText("");
            NewFlowerSupply.setText("");
            NewFlowerDesc.setText("");
            //Cleared the fields after sending request
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onNewFlowerNotification(NewFlowerNotification notif) {
        Platform.runLater(() -> {
            Flower newFlower = notif.getFlower();

            Tab selectedTab = MainTabsFrame.getSelectionModel().getSelectedItem();
            if (selectedTab == null) return;

            // Catalog tab: Add flower visually, with highlight
            if (selectedTab.getId().equals("FlowersTab")) {
                HBox flowerBox = new HBox(10);
                flowerBox.setStyle("-fx-border-color: #2fc4ca; -fx-background-color: #222;"); // Distinct border for new
                flowerBox.setPadding(new Insets(10));
                flowerBox.setAlignment(Pos.CENTER_LEFT);
                flowerBox.setPrefHeight(140);
                flowerBox.setMaxWidth(Double.MAX_VALUE);

                // [NEW] badge
                Label newBadge = new Label("[NEW]");
                newBadge.setStyle("-fx-background-color: #2fc4ca; -fx-text-fill: #222; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");

                // Image (placeholder, since none available)
                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                // You can use a "no_image.png" in your resources or just leave blank

                VBox textBox = new VBox(5);
                Label name = new Label("Name: " + newFlower.getName());
                name.setStyle("-fx-font-weight: bold;");
                Label price = new Label("Price: ₪" + newFlower.getPrice());
                Label color = new Label("Color: " + newFlower.getColor());
                Label desc = new Label("Description: " + newFlower.getDescription());
                Label supply = new Label("Supply: " + newFlower.getSupply());

                Spinner<Integer> quantitySpinner = new Spinner<>(1, newFlower.getSupply(), 1);
                quantitySpinner.setEditable(true);
                quantitySpinner.setMaxWidth(80);

                if (!Guest) {
                    Button addToCartButton = new Button("Add to Cart");
                    addToCartButton.setOnAction(e -> {
                        int amount = quantitySpinner.getValue();
                        cartMap.merge(newFlower, amount, Integer::sum);
                        System.out.println("Added to cart: " + newFlower.getName() + " x" + amount);
                    });
                    textBox.getChildren().addAll(newBadge, name, price, color, desc, supply, quantitySpinner, addToCartButton);
                } else {
                    textBox.getChildren().addAll(newBadge, name, price, color, desc, supply, quantitySpinner);
                }

                flowerBox.getChildren().addAll(imageView, textBox);

                // Optionally add at the top:
                FlowerPageVbox.getChildren().add(0, flowerBox);
                cachedFlowerNodes.add(new Pair<>(newFlower, flowerBox));

                // Scroll to top so user sees the new flower
                FlowersScrollPane.setVvalue(0.0);

            } else if (selectedTab.getId().equals("ManagerPanel")) {
                // Manager panel: ask server for updated catalog
                try {
                    SimpleClient.getClient().sendToServer("RequestFlowerCatalogForManager");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void onUpdateCreditCardResponse(UpdateCreditCardResponse response) {
        Platform.runLater(() -> {
            if (response.isSuccess()){
                account = response.getAccount();
                AccInfoCCNum.setText("Credit card number : " + account.getCreditCardNumber().toString());
                AccInfoCCV.setText("CCV : " + account.getCvv());
                AccInfoCCValidUntil.setText("CC valid until : " + account.getCreditCardValidUntil().toString());
                NewCardNumber.clear();
                NewCardCCV.clear();
            }
            NewCCError.setVisible(true);
            NewCCError.setText(response.getMessage());
        });
    }

    @Subscribe
    public void onCancelOrderResponse(CancelOrderResponse response) {
        Platform.runLater(() -> {
            Label errorLabel = orderErrorLabels.get(response.getOrderId());
            Button cancelButton = orderCancelButtons.get(response.getOrderId());
            if (response.isCancelled()) {
                // Remove the button from the UI
                if (cancelButton != null) {
                    ((Pane) cancelButton.getParent()).getChildren().remove(cancelButton);
                    orderCancelButtons.remove(response.getOrderId());
                }
                try {
                    SimpleClient.getClient().sendToServer(new GetUserOrdersRequest(account.getId()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (errorLabel != null) {
                    errorLabel.setText(response.getMessage());
                    errorLabel.setVisible(true);
                }
            }
        });
    }

    public void showCart() {
        CartVBox.getChildren().clear();
        double totalPrice = 0;
        boolean discountApplied = false;

        for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
            Flower flower = entry.getKey();
            int quantity = entry.getValue();
            double price = flower.getPrice() * quantity;

            totalPrice += price;

            HBox itemBox = new HBox(10);
            Label nameLabel = new Label(flower.getName() + " x" + quantity);
            Label priceLabel = new Label("₪" + price);

            Button plusBtn = new Button("+");
            Button minusBtn = new Button("-");

            plusBtn.setOnAction(e -> {
                int currentQty = cartMap.get(flower);
                if (currentQty + 1 <= flower.getSupply()) {
                    cartMap.put(flower, currentQty + 1);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Quantity Limit");
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot add more than available supply (" + flower.getSupply() + ").");

                    DialogPane dialogPane = alert.getDialogPane();
                    URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
                    if (cssUrl != null) {
                        dialogPane.getStylesheets().add(cssUrl.toExternalForm());
                    }
                    alert.showAndWait();
                }
                showCart();
            });

            minusBtn.setOnAction(e -> {
                int currentQty = cartMap.get(flower);
                if (currentQty > 1) {
                    cartMap.put(flower, currentQty - 1);
                } else {
                    cartMap.remove(flower);
                }
                showCart();
            });

            itemBox.getChildren().addAll(nameLabel, priceLabel, minusBtn, plusBtn);
            CartVBox.getChildren().add(itemBox);
        }

        // Apply discount if Plus member and total price > 50
        if (account != null && "Plus".equalsIgnoreCase(account.getSubscribtion_level()) && totalPrice > 50) {
            totalPrice *= 0.9; // Apply 10% discount
            discountApplied = true;
        }
        CartPriceLabel.setText("₪" + String.format("%.2f", totalPrice));
        DiscountLabel.setVisible(discountApplied);

        CartPriceLabel.setText("₪" + String.format("%.2f", totalPrice));
    }


    @FXML
    void initialize() {
        assert AccInfoCCNum != null : "fx:id=\"AccInfoCCNum\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoCCV != null : "fx:id=\"AccInfoCCV\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoCCValidUntil != null : "fx:id=\"AccInfoCCValidUntil\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoEmail != null : "fx:id=\"AccInfoEmail\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoPassword != null : "fx:id=\"AccInfoPassword\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoPhoneNum != null : "fx:id=\"AccInfoPhoneNum\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AddNewFlower != null : "fx:id=\"AddNewFlower\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CancelRenewButton != null : "fx:id=\"CancelRenewButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartAnchor != null : "fx:id=\"CartAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartPriceLabel != null : "fx:id=\"CartPriceLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartTab != null : "fx:id=\"CartTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CloseBtn != null : "fx:id=\"CloseBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ContinueToBuyButton != null : "fx:id=\"ContinueToBuyButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CustomTitleBar != null : "fx:id=\"CustomTitleBar\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CustomerServicePanel != null : "fx:id=\"CustomerServicePanel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackAnchor != null : "fx:id=\"FeedBackAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackDetails != null : "fx:id=\"FeedBackDetails\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackLabelText != null : "fx:id=\"FeedBackLabelText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackTab != null : "fx:id=\"FeedBackTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FeedBackTitle != null : "fx:id=\"FeedBackTitle\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowerPageVbox != null : "fx:id=\"FlowerPageVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersAnchor != null : "fx:id=\"FlowersAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersScrollPane != null : "fx:id=\"FlowersScrollPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FlowersTab != null : "fx:id=\"FlowersTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert FreeUserLabel != null : "fx:id=\"FreeUserLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert LogOutButton != null : "fx:id=\"LogOutButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MainTabsFrame != null : "fx:id=\"MainTabsFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerCatalogSelector != null : "fx:id=\"ManagerCatalogSelector\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerCatalogSelectorVbox != null : "fx:id=\"ManagerCatalogSelectorVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanel != null : "fx:id=\"ManagerPanel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelAnchor != null : "fx:id=\"ManagerPanelAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelPane != null : "fx:id=\"ManagerPanelPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ManagerPanelScroll != null : "fx:id=\"ManagerPanelScroll\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MinimizeBtn != null : "fx:id=\"MinimizeBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MyFeedBackScrollFrane != null : "fx:id=\"MyFeedBackScrollFrane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MyFeedBacksText != null : "fx:id=\"MyFeedBacksText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert MyFeedbacksVbox != null : "fx:id=\"MyFeedbacksVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewCCError != null : "fx:id=\"NewCCError\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewCardCCV != null : "fx:id=\"NewCardCCV\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewCardDate != null : "fx:id=\"NewCardDate\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewCardNumber != null : "fx:id=\"NewCardNumber\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewFlowerColor != null : "fx:id=\"NewFlowerColor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewFlowerDesc != null : "fx:id=\"NewFlowerDesc\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewFlowerName != null : "fx:id=\"NewFlowerName\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewFlowerPrice != null : "fx:id=\"NewFlowerPrice\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewFlowerSupply != null : "fx:id=\"NewFlowerSupply\" was not injected: check your FXML file 'secondary.fxml'.";
        assert NewPassError != null : "fx:id=\"NewPassError\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusLabelPayment != null : "fx:id=\"PlusLabelPayment\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUpgradeButton != null : "fx:id=\"PlusUpgradeButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUserLabel != null : "fx:id=\"PlusUserLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileSayHelloLabel != null : "fx:id=\"ProfileSayHelloLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileTabConfirmPassText != null : "fx:id=\"ProfileTabConfirmPassText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileTabNewPassText != null : "fx:id=\"ProfileTabNewPassText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryScrollFrame != null : "fx:id=\"PurchaseHistoryScrollFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryText != null : "fx:id=\"PurchaseHistoryText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryVbox != null : "fx:id=\"PurchaseHistoryVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PushFeedBack != null : "fx:id=\"PushFeedBack\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ResetMyPasswordButton != null : "fx:id=\"ResetMyPasswordButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ResolvedFeedbackVBOX != null : "fx:id=\"ResolvedFeedbackVBOX\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsAnchor != null : "fx:id=\"SettingsAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsPane != null : "fx:id=\"SettingsPane\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsTab != null : "fx:id=\"SettingsTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SortCatalogBtn != null : "fx:id=\"SortCatalogBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SubscribtionLevelLabel != null : "fx:id=\"SubscribtionLevelLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UnresolvedFeedbackVBOX != null : "fx:id=\"UnresolvedFeedbackVBOX\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpdateNewCC != null : "fx:id=\"UpdateNewCC\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartVBox != null : "fx:id=\"CartVBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpgradingAccountError != null : "fx:id=\"UpgradingAccountError\" was not injected: check your FXML file 'secondary.fxml'.";

        instance = this;

        // Drag support for custom bar:
        CustomTitleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        CustomTitleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) CustomTitleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        ManagerTabs = new Tab[] {
                ManagerPanel,
                CustomerServicePanel
        };

        EventBus.getDefault().register(this);
        FlowersScrollPane.setFitToWidth(true);
        FlowersScrollPane.setFitToHeight(false);
        FlowersScrollPane.setContent(FlowerPageVbox);
        FlowerPageVbox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMinHeight(Region.USE_COMPUTED_SIZE);
        FlowerPageVbox.setMaxHeight(Region.USE_COMPUTED_SIZE);

        MainTabsFrame.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == CartTab) {
                showCart();
            }
            if (newTab == FlowersTab) {
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
                System.out.println("Removing flowers");
                FlowerPageVbox.getChildren().clear();
                cachedFlowerNodes.clear();
            }
        });

        System.out.println("[SecondaryController] Initialized");

        App.notifySecondaryReady();
    }
}
