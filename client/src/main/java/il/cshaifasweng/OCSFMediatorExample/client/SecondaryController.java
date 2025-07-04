package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {

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
    private Label PlusLabelPayment;

    @FXML
    private Button PlusUpgradeButton;

    @FXML
    private Label PlusUserLabel;

    @FXML
    private Label ProfileSayHelloLabel;

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
    private Tab SettingsTab;

    @FXML
    private Button SortCatalogBtn;

    @FXML
    private Label SubscribtionLevelLabel;

    @FXML
    private VBox UnresolvedFeedbackVBOX;


    //==================CustomHeader=====================//

    // For window dragging
    private double xOffset = 0, yOffset = 0;

    private Tab[] ManagerTabs;

    private Account account;

    private boolean Guest;

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
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            VBox textBox = new VBox(5);
            Label name = new Label("Name: " + flower.getName());
            Label price = new Label("Price: ₪" + flower.getPrice());
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
            if(!Guest) {
                Button addToCartButton = new Button("Add to Cart");
                addToCartButton.setOnAction(e -> {
                    int amount = quantitySpinner.getValue();
                    cartList.add(new Pair<>(flower, amount));
                    System.out.println("Added to cart: " + flower.getName() + " x" + amount);
                });
                textBox.getChildren().addAll(name, price, desc, supply, quantitySpinner, addToCartButton);
            }else {
                textBox.getChildren().addAll(name, price, desc, supply, quantitySpinner);
            }
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
            });
        } else if ("Customer".equalsIgnoreCase(role)) {
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
            account = event.getAccount();
            Guest = account == null;
            if(!Guest) {
                System.out.println("Received sticky event for role: " + event.getAccount().getAccountLevel());
                ProfileSayHelloLabel.setText("Hello " + account.getFirstName() + " " + account.getLastName());
                System.out.println("Account Level: " + account.getAccountLevel());
            }else {
                System.out.println("Received sticky event for role: guest");
                ProfileSayHelloLabel.setText("Hello guest");
                System.out.println("Account Level: guest");

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

        for (Flower flower : flowerList) {
            VBox flowerBox = new VBox(5);
            flowerBox.setStyle("-fx-border-color: #4D8DFF; -fx-padding: 10;");
            flowerBox.setMaxWidth(Double.MAX_VALUE); // 🔁 makes it expand fully
            VBox.setVgrow(flowerBox, Priority.ALWAYS);

            Label nameLabel = new Label("Name: " + flower.getName());
            Label priceLabel = new Label("Price: ₪" + flower.getPrice());
            Label descLabel = new Label("Description: " + flower.getDescription());
            descLabel.setWrapText(true);

            Button editBtn = new Button("Edit");
            VBox editPane = new VBox(5);
            editPane.setVisible(false);
            editPane.setStyle("-fx-padding: 10;");
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
    void SortCatalog(ActionEvent event) {
        sortByPrice();
    }

    @FXML
    void ResetMyPassword(ActionEvent event) {

    }

    @FXML
    void ContinueToBuy(ActionEvent event) {

    }

    @FXML
    void UpgradeUserToPlus(ActionEvent event) {

    }

    @FXML
    void CancelAutoRenewSub(ActionEvent event) {

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
            } catch (Exception e) {
                e.printStackTrace();
            }
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

                Label nameLabel = (Label) textBox.getChildren().get(0);
                Label priceLabel = (Label) textBox.getChildren().get(1);
                Label descLabel = (Label) textBox.getChildren().get(2);
                Label supplyLabel = (Label) textBox.getChildren().get(3);
                Spinner<Integer> quantitySpinner = (Spinner<Integer>) textBox.getChildren().get(4);

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
        });
    }

    @FXML
    void initialize() {
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
        assert PlusLabelPayment != null : "fx:id=\"PlusLabelPayment\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUpgradeButton != null : "fx:id=\"PlusUpgradeButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUserLabel != null : "fx:id=\"PlusUserLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileSayHelloLabel != null : "fx:id=\"ProfileSayHelloLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryScrollFrame != null : "fx:id=\"PurchaseHistoryScrollFrame\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryText != null : "fx:id=\"PurchaseHistoryText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PurchaseHistoryVbox != null : "fx:id=\"PurchaseHistoryVbox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PushFeedBack != null : "fx:id=\"PushFeedBack\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ResetMyPasswordButton != null : "fx:id=\"ResetMyPasswordButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ResolvedFeedbackVBOX != null : "fx:id=\"ResolvedFeedbackVBOX\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsAnchor != null : "fx:id=\"SettingsAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SettingsTab != null : "fx:id=\"SettingsTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SortCatalogBtn != null : "fx:id=\"SortCatalogBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SubscribtionLevelLabel != null : "fx:id=\"SubscribtionLevelLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UnresolvedFeedbackVBOX != null : "fx:id=\"UnresolvedFeedbackVBOX\" was not injected: check your FXML file 'secondary.fxml'.";

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
