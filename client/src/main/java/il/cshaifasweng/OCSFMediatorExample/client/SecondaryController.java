package il.cshaifasweng.OCSFMediatorExample.client;

import java.awt.image.BufferedImage;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javassist.Loader;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.*;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.Subscribe;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class SecondaryController {

    @FXML
    public StackPane imageDropZone;

    @FXML
    public ImageView flowerImageView;

    @FXML
    public Label dropHint;

    @FXML
    public Button clearImageBtn;

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private Button generateReportBtn;

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
    private Label ManagerBranch;

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
    private Pane PaneNewFlower;

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
    private Label NewPassError;

    @FXML
    private Label PlusLabelPayment;

    @FXML
    private Button PlusUpgradeButton;

    @FXML
    private TextField NewStorageSupply;

    @FXML
    private Label PlusUserLabel;

    @FXML
    private Label ProfileSayHelloLabel;

    @FXML
    private Label CartMessage;

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
    private TextField NewHaifaFlowerSupply;

    @FXML
    private TextField NewTelAvivFlowerSupply;

    @FXML
    private TextField NewEilatFlowerSupply;

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

    @FXML
    private HBox branchSelectorBox;

    @FXML
    private Label managerScopeLabel;

    @FXML
    private ComboBox<Branch> branchSelectorComboBox;

    @FXML
    private ComboBox<String> FeedBackBranch;

    // ====================================

    @FXML
    private Tab detailsChange;

    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, String> creditCard;

    @FXML
    private TableColumn<Account, String> email;

    @FXML
    private TableColumn<Account, String> id;

    @FXML
    private TableColumn<Account, String> name;

    @FXML
    private TableColumn<Account, String> password;

    @FXML
    private TableColumn<Account, String> phone;

    @FXML
    private TableColumn<Account, String> sub;

    @FXML
    private TableColumn<Account, String> accountLevel;

    @FXML
    private TableColumn<Account, String> branchID;

    @FXML
    private AnchorPane window;

    @FXML
    private ComboBox<String> BranchComboBox;

    @FXML
    private DatePicker reportDate1;
    @FXML
    private DatePicker reportDate2;
    @FXML
    private Button compareReportsBtn;


    @FXML
    private TableView<il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportResponse.Row> QuarterlyTable;
    @FXML
    private TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportResponse.Row, String> colYear;
    @FXML
    private TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportResponse.Row, String> colQuarter;
    @FXML
    private TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportResponse.Row, String> colRevenue;

    //==================CustomHeader=====================//

    private double xOffset = 0, yOffset = 0;

    private Tab[] ManagerTabs;

    private Account account;

    private boolean Guest;

    private List<Pair<Flower, HBox>> cachedFlowerNodes = new ArrayList<>();

    private final Map<Flower, Integer> cartMap = new HashMap<>();

    private String Sorted = "Unsorted";

    private byte[] flowerJpeg;

    private List<OrderSQL> purchaseHistoryList = new ArrayList<>();

    public static SecondaryController instance;

    public Map<Flower, Integer> getCartMap() {
        return cartMap;
    }

    private void setupManagerUI() {
        if (account == null) {
            return;
        }

        String accountLevel = account.getAccountLevel();

        if ("Branch Manager".equalsIgnoreCase(accountLevel)) {
            branchSelectorBox.setVisible(true);
            managerScopeLabel.setVisible(true);
            branchSelectorComboBox.setVisible(false);
            branchSelectorComboBox.setManaged(false); // Ensures it doesn't take up space

            if (account.getBranch() != null) {
                managerScopeLabel.setText("Reports for: " + account.getBranch().getName());
            } else {
                managerScopeLabel.setText("Error: No branch assigned!");
            }
        } else if ("Network Manager".equalsIgnoreCase(accountLevel)) {
            branchSelectorBox.setVisible(true);
            managerScopeLabel.setVisible(false);
            managerScopeLabel.setManaged(false); // Ensures it doesn't take up space
            branchSelectorComboBox.setVisible(true);

            // **NEW:** Ask the server for the list of all branches
            try {
                SimpleClient.getClient().sendToServer(new GetAllBranchesRequest());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            branchSelectorBox.setVisible(false);
        }
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
            orderErrorLabels.clear();

            for (OrderSQL order : purchaseHistoryList) {
                VBox orderBox = new VBox(5);
                orderBox.setStyle("-fx-padding: 10; -fx-border-color: #4D8DFF; -fx-border-radius: 8; -fx-background-radius: 8;");

                // Basic info
                Label detailsLabel = new Label("Details: " + order.getDetails());
                Label priceLabel = new Label("Price: ₪" + order.getTotalPrice());
                Label statusLabel = new Label("Status: " + order.getStatus());

                // Date (date-only)
                String dateOnly;
                try {
                    java.text.DateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dateOnly = order.getDeliveryDate() != null ? fmt.format(order.getDeliveryDate()) : "—";
                } catch (Exception ex) {
                    dateOnly = "—";
                }
                Label dateLabel = new Label("Date: " + dateOnly);

                // Time
                Label timeLabel = new Label("Time: " + (order.getDeliveryTime() != null ? order.getDeliveryTime() : "—"));

                // Fulfillment (Delivery vs Pickup)
                String fulfillmentText = order.getPickupBranch() == 4 ? "Delivery" : "Pick-up";

                try {
                    if (order.getPickupBranch() != 4) {
                        Integer bid = order.getPickupBranch();
                        String bname = "Unknown";
                        if (bid != null) {
                            switch (bid) {
                                case 1:
                                    bname = "Haifa";
                                    break;
                                case 2:
                                    bname = "Eilat";
                                    break;
                                case 3:
                                    bname = "Tel Aviv";
                                    break;
                                default:
                                    bname = "Network";
                                    break;
                            }
                        }
                        fulfillmentText = "Pickup: " + bname;
                    }
                } catch (Exception ignored) {
                    // lazy/detached entity or null branch
                }

                Label fulfillmentLabel = new Label("Fulfillment: " + fulfillmentText);


                orderBox.getChildren().addAll(detailsLabel, priceLabel, statusLabel, fulfillmentLabel);

                // Address for deliveries only
                if ("Delivery".equals(fulfillmentText)
                        && order.getAddress() != null
                        && !order.getAddress().isBlank()) {
                    orderBox.getChildren().add(new Label("Address: " + order.getAddress()));
                }

                orderBox.getChildren().addAll(dateLabel, timeLabel);

                // If canceled, show refunded amount
                boolean isCanceled = order.getStatus() != null && order.getStatus().equalsIgnoreCase("canceled");
                if (isCanceled) {
                    Double refund = order.getRefundAmount();
                    String refundText = (refund != null) ? String.format("₪%.2f", refund) : "₪0.00";
                    Label refundLabel = new Label("Refunded amount: " + refundText);
                    refundLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    orderBox.getChildren().add(refundLabel);
                }

                // Error label (per order)
                Label errorLabel = new Label();
                errorLabel.setVisible(false);
                orderErrorLabels.put(order.getId(), errorLabel);
                orderBox.getChildren().add(errorLabel);

                // Cancel button (hide if already canceled)
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(e -> {
                    try {
                        SimpleClient.getClient().sendToServer(new CancelOrderRequest(order.getId()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                if (!isCanceled) {
                    orderBox.getChildren().add(cancelButton);
                    orderCancelButtons.put(order.getId(), cancelButton);
                }

                // Keep card auto-update behavior
                setupOrderCard(order, statusLabel, cancelButton);

                PurchaseHistoryVbox.getChildren().add(orderBox);
            }
        });
    }

    /**
     * Schedules regular checks for order status
     */
    private void setupOrderCard(OrderSQL order, Label statusLabel, Button cancelButton) {
        updateOrderStatus(order, statusLabel, cancelButton);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> updateOrderStatus(order, statusLabel, cancelButton))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Marks as delivered & hides cancel after delivery time
     */
    private void updateOrderStatus(OrderSQL order, Label statusLabel, Button cancelButton) {
        try {
            LocalDate orderDate = (order.getDeliveryDate() instanceof java.sql.Date)
                    ? ((java.sql.Date) order.getDeliveryDate()).toLocalDate()
                    : order.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalTime orderTime = LocalTime.parse(order.getDeliveryTime());
            LocalDateTime orderDateTime = LocalDateTime.of(orderDate, orderTime);
//the status is changed auto check every 30 secs
            if (LocalDateTime.now().isAfter(orderDateTime)) {
                statusLabel.setText("status: delivered");
                if (cancelButton != null) cancelButton.setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

            // Image
            ImageView imageView = new ImageView();
            try {
                if (imageData != null && imageData.length > 0) {
                    imageView.setImage(new Image(new ByteArrayInputStream(imageData)));
                } else {
                    imageView.setImage(null); // no picture; leave empty or set a placeholder if you have one
                }
            } catch (Exception e) {
                System.err.println("Failed to load image for: " + flower.getName());
                e.printStackTrace();
            }
            System.out.println("Processing: " + (flower == null ? "null" : flower.getName()));
            System.out.println("Image data null? " + (imageData == null));

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            // Text / details
            VBox textBox = new VBox(5);
            Label name  = new Label("Name: " + flower.getName());
            Label color = new Label("Color: " + flower.getColor());
            Label desc  = new Label("Description: " + flower.getDescription());
            desc.setWrapText(true);
            Label supply = new Label("Supply: " + flower.getSupply());

            // --- Price / Discount ---
            int discount = Math.max(0, Math.min(100, flower.getDiscount())); // clamp just in case
            double priceValue = flower.getPrice();
            Node priceNode;
            if (discount > 0) {
                double discounted = java.math.BigDecimal.valueOf(priceValue)
                        .multiply(java.math.BigDecimal.valueOf(100 - discount))
                        .divide(java.math.BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP)
                        .doubleValue();

                javafx.scene.text.Text orig = new javafx.scene.text.Text("Price: ₪" + String.format("%.2f", priceValue));
                orig.setStrikethrough(true);
                orig.setFill(javafx.scene.paint.Color.WHITE);

                Label disc = new Label("DISCOUNT PRICE: ₪" + String.format("%.2f", discounted) + " (" + discount + "% off)");
                disc.setStyle("-fx-text-fill: #23c55e; -fx-font-weight: bold;");

                HBox priceRow = new HBox(8, orig, disc);
                priceRow.setAlignment(Pos.CENTER_LEFT);
                priceNode = priceRow;
            } else {
                priceNode = new Label("Price: ₪" + String.format("%.2f", priceValue));
            }

            if (flower.getSupply() <= 0) {
                Label outOfStock = new Label("Out of Stock");
                outOfStock.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                textBox.getChildren().addAll(name, priceNode, color, desc, supply, outOfStock);
            } else {
                Spinner<Integer> quantitySpinner = new Spinner<>(1, flower.getSupply(), 1);
                TextField spinnerEditor = quantitySpinner.getEditor();
                spinnerEditor.setTextFormatter(new TextFormatter<String>(change -> {
                    String newText = change.getControlNewText();
                    return newText.matches("\\d*") ? change : null;
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
                    textBox.getChildren().addAll(name, priceNode, color, desc, supply, quantitySpinner, addToCartButton);
                } else {
                    textBox.getChildren().addAll(name, priceNode, color, desc, supply, quantitySpinner);
                }
            }

            System.out.println("Adding node for: " + flower.getName());
            flowerBox.getChildren().addAll(imageView, textBox);

            cachedFlowerNodes.add(new Pair<>(flower, flowerBox));
            FlowerPageVbox.getChildren().add(flowerBox);
        }

        Platform.runLater(() -> FlowersScrollPane.setVvalue(0.0));
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

    public String resolveBranchName(int number) {
        return switch (number) {
            case 1 -> "Haifa";
            case 2 -> "Eilat";
            case 3 -> "Tel-Aviv";
            default -> null;
        };
    }

    public void setUserRole() {
        String role = account == null ? "Guest" : account.getAccountLevel();
        System.out.println("role: " + role);
        if ("Guest".equalsIgnoreCase(role)) {
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
        } else if ("Customer".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                for (Tab tab : ManagerTabs) {
                    MainTabsFrame.getTabs().remove(tab);
                }
            });
        } else if ("BranchManager".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                MainTabsFrame.getTabs().remove(CustomerServicePanel);
                MainTabsFrame.getTabs().remove(detailsChange);
                MainTabsFrame.getTabs().remove(CustomerServicePanel);
                ManagerBranch.setText("Branch: " + resolveBranchName(resolveBranchId(account)));
                if (PaneNewFlower != null && PaneNewFlower.getParent() instanceof VBox vbox) {
                    vbox.getChildren().remove(PaneNewFlower);
                }
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
                BranchComboBox.setDisable(true);
                BranchComboBox.setVisible(false);
            });
        } else if ("CustomerService".equalsIgnoreCase(role)) {
            Platform.runLater(() -> {
                for (Tab tab : ManagerTabs) {
                    if (tab != CustomerServicePanel) {
                        MainTabsFrame.getTabs().remove(tab);
                    }
                }
            });
        } else if ("NetworkManager".equalsIgnoreCase(role)) {
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
        } else {
            System.out.println("Unknown role: " + role);
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
        javafx.application.Platform.runLater(() -> {
            account = event.getAccount();
            setupManagerUI();
            Guest = account == null;
            if (!Guest) {
                System.out.println("Received sticky event for role: " + event.getAccount().getAccountLevel());
                ProfileSayHelloLabel.setText("Hello " + account.getFirstName() + " " + account.getLastName());
                System.out.println("Account Level: " + account.getAccountLevel() + ", user : " + account.getSubscribtion_level());
                SubscribtionLevelLabel.setText(account.getSubscribtion_level() + " user");
                if (account.getSubscribtion_level().equals("Free")) {
                    FreeUserLabel.setVisible(true);
                    CancelRenewButton.setVisible(false);
                    PlusUserLabel.setVisible(false);
                } else {
                    FreeUserLabel.setVisible(false);
                    PlusUpgradeButton.setVisible(false);
                    if (account.getAuto_renew_subscription().equals("Yes")) {
                        CancelRenewButton.setVisible(true);
                        PlusUserLabel.setText("Renew at " + account.getSubscription_expires_at());
                    } else {
                        CancelRenewButton.setVisible(false);
                        PlusUserLabel.setText("Expires at " + account.getSubscription_expires_at());
                    }
                }
                try {
                    SimpleClient.getClient().sendToServer(new GetUserOrdersRequest(account.getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
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
            if (!Guest) {
                SimpleClient.getClient().sendToServer(new LogoutRequest(account));
            }
            account = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        // 1) Optional: hide window immediately so the UI feels snappy
        Stage stage = (Stage) CustomTitleBar.getScene().getWindow();
        stage.hide();

        // 2) Unregister from EventBus to stop callbacks into a dead controller
        try {
            org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
        } catch (Throwable ignore) { /* already unregistered */ }

        // 3) Tell server we're out, then close the socket
        if (!Guest) {
            try {
                SimpleClient.getClient().sendToServer(new LogoutRequest(account));
                SimpleClient.getClient().sendToServer("remove client");
            } catch (IOException ex) {
                // log, don't rethrow
                System.err.println("Logout send failed: " + ex.getMessage());
            }
        }
        account = null;

        try {
            // If your OCSF client exposes this — prevents lingering non-daemon threads
            SimpleClient.getClient().closeConnection();
        } catch (Exception ex) {
            System.err.println("Close connection failed: " + ex.getMessage());
        }

        // 4) Shut down any app executors/timers you created
        // e.g., scheduler.shutdownNow();

        // 5) Exit JavaFX cleanly; normalize exit code for maven javafx:run
        Platform.exit();
        // If you still see the plugin reporting a weird Windows error code, uncomment:
        // System.exit(0);
    }

    @FXML
    private void minimizeWindow() {
        ((Stage) CustomTitleBar.getScene().getWindow()).setIconified(true);
    }

    // Branch ids you already use:
    private static final int BRANCH_HAIFA = 1;
    private static final int BRANCH_EILAT = 2;
    private static final int BRANCH_TEL_AVIV = 3;

    public void populateManagerCatalog(List<Flower> flowerList) {
        ManagerCatalogSelectorVbox.getChildren().clear();
        ManagerCatalogSelector.setFitToWidth(true);

        String role = (account != null && account.getAccountLevel() != null)
                ? account.getAccountLevel().replaceAll("\\s+", "").toLowerCase()
                : "";

        final boolean isNetworkManager = role.equals("networkmanager") || role.equals("managernetwork");
        final boolean isBranchManager = role.equals("branchmanager") || role.equals("managerbranch");
        final boolean isManagerAll = isNetworkManager || role.equals("manager");
        final int branchId = resolveBranchId(account);

        for (Flower flower : flowerList) {
            VBox card = new VBox(8);
            card.setStyle("-fx-border-color: #4D8DFF; -fx-padding: 14 16 14 16; -fx-background-radius: 8;");
            card.setMaxWidth(470);
            card.prefWidthProperty().bind(ManagerCatalogSelectorVbox.widthProperty().subtract(24));

            Label name = new Label("Name: " + flower.getName());
            Label price = new Label("Price: ₪" + flower.getPrice() + (flower.getDiscount() != 0?", with discount: ₪"+ flower.getPrice()*(100 - flower.getDiscount())/100:""));
            Label discount = new Label("discount: %" + flower.getDiscount());
            Label color = new Label("Color: " + flower.getColor());

            Label total = new Label();
            updateTotalAndModel(flower, total);

            Label desc = new Label("Description: " + flower.getDescription());
            desc.setWrapText(true);

            HBox haifaRow = buildSupplyRow("Supply (Haifa): ", flower.getSupplyHaifa(),
                    isManagerAll || (isBranchManager && branchId == BRANCH_HAIFA),
                    newVal -> {
                        flower.setSupplyHaifa(newVal);
                        updateTotalAndModel(flower, total);
                        pushUpdateAndRefresh(flower, flowerList);
                    });

            HBox eilatRow = buildSupplyRow("Supply (Eilat): ", flower.getSupplyEilat(),
                    isManagerAll || (isBranchManager && branchId == BRANCH_EILAT),
                    newVal -> {
                        flower.setSupplyEilat(newVal);
                        updateTotalAndModel(flower, total);
                        pushUpdateAndRefresh(flower, flowerList);
                    });

            HBox telAvivRow = buildSupplyRow("Supply (Tel Aviv): ", flower.getSupplyTelAviv(),
                    isManagerAll || (isBranchManager && branchId == BRANCH_TEL_AVIV),
                    newVal -> {
                        flower.setSupplyTelAviv(newVal);
                        updateTotalAndModel(flower, total);
                        pushUpdateAndRefresh(flower, flowerList);
                    });

            HBox storageRow = buildSupplyRow("Supply (Delivery): ", flower.getStorage(),
                    isManagerAll,
                    newVal -> {
                        flower.setStorage(newVal);
                        updateTotalAndModel(flower, total);
                        pushUpdateAndRefresh(flower, flowerList);
                    });

            HBox actions = new HBox(8);
            VBox editPane = new VBox(10);
            editPane.setStyle("-fx-background-color: #242b3b; -fx-padding: 12; -fx-background-radius: 0 0 8 8;");
            editPane.setMaxWidth(Double.MAX_VALUE);
            editPane.setVisible(false);
            editPane.setManaged(false);

            if (isManagerAll) {
                Button editBtn = new Button("Edit");
                Button deleteBtn = new Button("Delete");
                actions.getChildren().addAll(editBtn, deleteBtn);

                TextField nameField = new TextField(flower.getName());
                TextField colorField = new TextField(flower.getColor());
                TextField priceField = new TextField(String.valueOf(flower.getPrice()));
                TextField discountField = new TextField(String.valueOf(flower.getDiscount()));
                TextArea descField = new TextArea(flower.getDescription());
                descField.setPrefHeight(80);
                descField.setWrapText(true);
                descField.setMaxWidth(Double.MAX_VALUE);

                Button saveBtn = new Button("Save");
                Button cancelBtn = new Button("Cancel");
                HBox buttonBox = new HBox(8, saveBtn, cancelBtn);

                // --- NetworkManager-only picture controls
                final java.util.concurrent.atomic.AtomicReference<byte[]> pendingImageJpeg =
                        new java.util.concurrent.atomic.AtomicReference<>(null);
                final java.util.concurrent.atomic.AtomicBoolean deleteImageFlag =
                        new java.util.concurrent.atomic.AtomicBoolean(false);

                Button changePicBtn;
                Button removePicBtn;

                if (isNetworkManager) {
                    changePicBtn = new Button("Change picture…");
                    removePicBtn = new Button("Remove picture");

                    Button finalChangePicBtn = changePicBtn;
                    changePicBtn.setOnAction(ev -> {
                        FileChooser fc = new FileChooser();
                        fc.setTitle("Choose JPG");
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Images", "*.jpg", "*.jpeg"));
                        File f = fc.showOpenDialog(ManagerCatalogSelector.getScene().getWindow());
                        if (f != null) {
                            try {
                                pendingImageJpeg.set(prepareJpegPayload(f)); // must exist in your controller
                                deleteImageFlag.set(false);
                                finalChangePicBtn.setText("Picture selected ✓");
                                if (removePicBtn != null) removePicBtn.setText("Remove picture");
                            } catch (Exception ex) {
                                new Alert(Alert.AlertType.ERROR, "Only JPG allowed.\n" + ex.getMessage()).showAndWait();
                                pendingImageJpeg.set(null);
                                finalChangePicBtn.setText("Change picture…");
                            }
                        }
                    });

                    Button finalRemovePicBtn = removePicBtn;
                    removePicBtn.setOnAction(ev -> {
                        // mark for deletion
                        deleteImageFlag.set(true);
                        pendingImageJpeg.set(null);
                        if (finalRemovePicBtn != null) finalRemovePicBtn.setText("Will remove ✓");
                        if (changePicBtn != null) changePicBtn.setText("Change picture…");
                    });
                } else {
                    removePicBtn = null;
                    changePicBtn = null;
                }

                // Save
                Button finalChangePicBtn1 = changePicBtn;
                Button finalRemovePicBtn1 = removePicBtn;
                saveBtn.setOnAction(e -> {
                    String newName = nameField.getText().trim();
                    String newColor = colorField.getText().trim();
                    String priceTxt = priceField.getText().trim();
                    String discountTxt = discountField.getText().trim();
                    String newDesc = descField.getText().trim();

                    boolean valid = true;
                    if (newName.isEmpty() || !newName.matches("^[A-Za-z\\s]+$")) {
                        nameField.setStyle("-fx-border-color: red;");
                        valid = false;
                    } else nameField.setStyle("");
                    if (newColor.isEmpty() || !newColor.matches("^[A-Za-z\\s]+$")) {
                        colorField.setStyle("-fx-border-color: red;");
                        valid = false;
                    } else colorField.setStyle("");
                    if (!priceTxt.matches("^\\d+(\\.\\d{1,2})?$")) {
                        priceField.setStyle("-fx-border-color: red;");
                        valid = false;
                    } else priceField.setStyle("");
                    try {
                        int d = Integer.parseInt(discountTxt);
                        if (d < 0 || d > 100) {
                            discountField.setStyle("-fx-border-color: red;");
                            valid = false;
                        } else {
                            discountField.setStyle("");
                        }
                    } catch (NumberFormatException ex) {
                        discountField.setStyle("-fx-border-color: red;");
                        valid = false;
                    }

                    if (!valid) return;

                    double newPrice = Double.parseDouble(priceTxt);
                    int d = Integer.parseInt(discountTxt);

                    flower.setName(newName);
                    flower.setColor(newColor);
                    flower.setPrice(newPrice);
                    flower.setDescription(newDesc);
                    flower.setDiscount(d);

                    try {
                        byte[] img = pendingImageJpeg.get(); // may be null
                        boolean del = deleteImageFlag.get();
                        String suggested = sanitizeBaseName(newName) + ".jpg";
                        SimpleClient.getClient().sendToServer(
                                new UpdateFlowerRequest(flower, img, suggested, del)
                        );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (finalChangePicBtn1 != null) finalChangePicBtn1.setText("Change picture…");
                    if (finalRemovePicBtn1 != null) finalRemovePicBtn1.setText("Remove picture");
                    editPane.setVisible(false);
                    editPane.setManaged(false);
                    populateManagerCatalog(flowerList);
                });

                cancelBtn.setOnAction(e -> {
                    editPane.setVisible(false);
                    editPane.setManaged(false);
                });

                // Delete flower
                deleteBtn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText(null);
                    alert.setGraphic(null);
                    alert.setContentText("Are you sure you want to delete this flower?\n\nFlower name: " + flower.getName());
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            SimpleClient.getClient().sendToServer(new DeleteFlowerRequest(flower.getId()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        flowerList.remove(flower);
                        populateManagerCatalog(flowerList);
                    }
                });

                editBtn.setOnAction(e -> {
                    boolean now = !editPane.isVisible();
                    editPane.setVisible(now);
                    editPane.setManaged(now);
                    if (now) nameField.requestFocus();
                });

                if (isNetworkManager) {
                    HBox picButtons = new HBox(8, changePicBtn, removePicBtn);
                    editPane.getChildren().add(picButtons);
                }
                editPane.getChildren().addAll(
                        new Label("Edit Name:"), nameField,
                        new Label("Edit Price:"), priceField,
                        new Label("Edit Discount:"), discountField,
                        new Label("Edit Color:"), colorField,
                        new Label("Edit Description:"), descField,
                        buttonBox
                );
            } else {
                actions.setManaged(false);
                actions.setVisible(false);
            }

            card.getChildren().addAll(
                    name, price, discount,color,
                    haifaRow, eilatRow, telAvivRow, storageRow,
                    total,
                    desc
            );
            if (isManagerAll) card.getChildren().addAll(actions, editPane);

            ManagerCatalogSelectorVbox.getChildren().add(card);
        }

        ManagerCatalogSelectorVbox.setFillWidth(true);
    }

    // ===== HELPERS =====
    private String sanitizeBaseName(String s) {
        String base = s.toLowerCase().trim().replaceAll("\\s+", "-");
        base = base.replaceAll("[^a-z0-9._-]", "_");
        return base.isBlank() ? "image" : base;
    }

    // Safe branch-id read (works even if Branch is a lazy proxy)
    private int resolveBranchId(Account acc) {
        if (acc == null) return 0;
        Branch br = acc.getBranch();
        if (br == null) return 0;
        // The proxy always has the identifier available without needing a Hibernate Session
        return br.getId();
    }

    /**
     * Build a row:  [Label "Supply (X): <value>"]  [Change Supply] (button can be hidden)
     * onSave is called with a validated non-negative int.
     */
    private HBox buildSupplyRow(String labelPrefix,
                                int initialValue,
                                boolean canEdit,
                                java.util.function.IntConsumer onSave) {
        Label valueLabel = new Label(labelPrefix + initialValue);
        Button changeBtn = new Button("Change Supply");

        if (!canEdit) {
            // Hide and remove from layout if not allowed
            changeBtn.setVisible(false);
            changeBtn.setManaged(false);
        } else {
            changeBtn.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog(String.valueOf(initialValue));
                dialog.setTitle("Update Supply");
                dialog.setHeaderText(null);
                dialog.setContentText("Enter new amount (≥ 0):");
                dialog.getDialogPane().getScene().getWindow().sizeToScene();

                dialog.showAndWait().ifPresent(input -> {
                    try {
                        int v = Integer.parseInt(input.trim());
                        if (v < 0) throw new NumberFormatException();
                        valueLabel.setText(labelPrefix + v);
                        onSave.accept(v);
                    } catch (NumberFormatException ex) {
                        // simple inline feedback; replace with your styled alert if you prefer
                        Alert a = new Alert(Alert.AlertType.WARNING, "Please enter a non-negative integer.", ButtonType.OK);
                        a.setHeaderText(null);
                        a.showAndWait();
                    }
                });
            });
        }

        HBox row = new HBox(8, valueLabel, changeBtn);
        return row;
    }

    /**
     * Recalculate total (Haifa + Eilat + TelAviv + Storage), write label, and keep model’s total in sync.
     */
    private void updateTotalAndModel(Flower f, Label totalLabel) {
        int total = (f.getSupplyHaifa() + f.getSupplyEilat() + f.getSupplyTelAviv() + f.getStorage());
        f.setSupply(total); // keep the 'supply' column as the grand total
        totalLabel.setText("Total Supply: " + total);
    }

    /**
     * Send the update to the server and refresh the UI immediately so the new value is visible
     * without a manual refresh. (This keeps the live feel in the manager panel.)
     */
    private void pushUpdateAndRefresh(Flower flower, List<Flower> currentList) {
        try {
            SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Rebuild the list immediately with the updated in-memory values
        populateManagerCatalog(currentList);
    }

    /* ---------- Helpers ---------- */

    private static class Row {
        final HBox box;
        final Label label;

        Row(HBox b, Label l) {
            this.box = b;
            this.label = l;
        }
    }

    /**
     * Prompts for a non-negative integer; returns Optional.empty() if invalid/canceled.
     */
    private Optional<Integer> promptForInt(String title, String content, int current) {
        TextInputDialog d = new TextInputDialog(String.valueOf(current));
        d.setHeaderText(null);
        d.setTitle(title);
        d.setContentText(content);
        Optional<String> res = d.showAndWait();
        if (res.isEmpty()) return Optional.empty();
        try {
            int val = Integer.parseInt(res.get().trim());
            if (val < 0) throw new NumberFormatException();
            return Optional.of(val);
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid non-negative integer.").showAndWait();
            return Optional.empty();
        }
    }

    /**
     * Sends update to server (fire-and-forget). Keep optimistic UI already updated.
     */
    private void pushUpdate(Flower flower) {
        try {
            SimpleClient.getClient().sendToServer(new UpdateFlowerRequest(flower));
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to push update to server.").showAndWait();
        }
    }


    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static int nz(Integer i) {
        return i == null ? 0 : i;
    }


    private void updateTotal(Flower flower, Label totalLabel) {
        int total = flower.getSupplyHaifa() + flower.getSupplyEilat() + flower.getSupplyTelAviv() + flower.getStorage();
        flower.setSupply(total);
        totalLabel.setText("Total Supply: " + total);
    }


    private HBox buildSupplyRow(
            String labelText,
            int currentValue,
            boolean canEdit,
            String supplyKey,
            java.util.function.IntConsumer onChange
    ) {
        Label label = new Label(labelText + currentValue);
        Button changeBtn = new Button("Change Supply");
        changeBtn.setDisable(!canEdit);

        changeBtn.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog(String.valueOf(currentValue));
            dlg.setTitle("Update Supply");
            dlg.setHeaderText("Set new supply for " + supplyKey);
            dlg.setContentText("Enter a non-negative integer:");
            dlg.showAndWait().ifPresent(txt -> {
                try {
                    int val = Integer.parseInt(txt.trim());
                    if (val < 0) throw new NumberFormatException();
                    onChange.accept(val);
                    label.setText(labelText + val);
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.WARNING, "Please enter a non-negative integer.").showAndWait();
                }
            });
        });

        return new HBox(8, label, changeBtn);
    }


    private void promptAndUpdateSupply(String branchLabel, int current, java.util.function.Consumer<Integer> onValid) {
        TextInputDialog dlg = new TextInputDialog(String.valueOf(current));
        dlg.setTitle("Change Supply");
        dlg.setHeaderText(null);
        dlg.setContentText("New supply for " + branchLabel + " (>= 0):");
        Optional<String> res = dlg.showAndWait();
        if (res.isEmpty()) return;

        try {
            int val = Integer.parseInt(res.get().trim());
            if (val < 0) throw new NumberFormatException();
            onValid.accept(val);
        } catch (NumberFormatException nfe) {
            new Alert(Alert.AlertType.ERROR, "Please enter a non-negative integer.", ButtonType.OK).showAndWait();
        }
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
        } else {
            System.err.println("Something went wrong. Please try again.");
            System.out.println("CC: " + account.getCreditCardNumber());
            System.out.println("CVV: " + account.getCvv());
            System.out.println("ValidUntil: " + account.getCreditCardValidUntil());
            System.out.println("isCardStillValid: " + isCardStillValid(account));
            if (isCardStillValid(account) == false) {
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
        Date validUntil = (Date) account.getCreditCardValidUntil();
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
        if (!account.getSubscribtion_level().equals("Free") && account.getAuto_renew_subscription().equals("Yes")) {
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
                VBox feedbackBox = new VBox(5);
                feedbackBox.setStyle(
                        "-fx-padding: 10;" +
                                "-fx-border-color: #4D8DFF;" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;"
                );
                feedbackBox.setMinHeight(Region.USE_PREF_SIZE);
                feedbackBox.setFocusTraversable(false);

                // Title
                Label titleLabel = new Label("Title: " + feedback.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                // Details
                Label descLabel = new Label("Details: " + feedback.getDetails());
                descLabel.setWrapText(true);

                // From (email)
                Label emailLabel = new Label("From: " + feedback.getAccount().getEmail());
                emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2196F3;");

                // Branch (safe against null/lazy)
                String branchText = "—";
                Branch branch = feedback.getBranch();
                if (branch != null) {
                    int branchId = branch.getId();  // safe, ID is available even if lazy
                    switch (branchId) {
                        case BRANCH_HAIFA:
                            branchText = "Haifa";
                            break;
                        case BRANCH_EILAT:
                            branchText = "Eilat";
                            break;
                        case BRANCH_TEL_AVIV:
                            branchText = "Tel Aviv";
                            break;
                        default:
                            branchText = "Unknown";
                            break;
                    }
                }

                Label branchLabel = new Label("Branch: " + branchText);
                branchLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #B0B9FF;");


                // Submitted at
                Label sentLabel = new Label("Sent: " + feedback.getSubmittedAt());
                sentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");

                feedbackBox.getChildren().addAll(titleLabel, descLabel, emailLabel, branchLabel, sentLabel);

                if (feedback.getStatus() == FeedBackSQL.FeedbackStatus.Pending) {
                    // Action buttons for pending
                    HBox buttons = new HBox(10);

                    Button rejectBtn = new Button("Reject");
                    rejectBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
                    rejectBtn.setOnAction(e -> markFeedbackRejected(feedback));

                    Button doneBtn = new Button("Done");
                    doneBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                    doneBtn.setOnAction(e -> markFeedbackResolved(feedback));

                    buttons.getChildren().addAll(rejectBtn, doneBtn);
                    feedbackBox.getChildren().add(buttons);

                    UnresolvedFeedbackVBOX.getChildren().add(feedbackBox);
                } else {
                    // Status & resolved time for non-pending
                    boolean isResolved = feedback.getStatus() == FeedBackSQL.FeedbackStatus.Resolved;
                    String statusText = isResolved ? "Resolved" : "Rejected";
                    String statusColor = isResolved ? "#4CAF50" : "#F44336";

                    Label statusLabel = new Label("Status: " + statusText);
                    statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");
                    feedbackBox.getChildren().add(statusLabel);

                    if (feedback.getResolvedAt() != null) {
                        Label resolvedLabel = new Label("At: " + feedback.getResolvedAt());
                        resolvedLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
                        feedbackBox.getChildren().add(resolvedLabel);
                    }

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
        FeedBackBranch.setStyle("");

        String title = FeedBackTitle.getText().trim();
        String details = FeedBackDetails.getText().trim();
        String branch = FeedBackBranch.getValue(); // 👈 get selected branch

        // Validation
        if (title.isEmpty()) {
            FeedBackTitle.setStyle("-fx-border-color: red;");
            valid = false;
        }
        if (details.isEmpty()) {
            FeedBackDetails.setStyle("-fx-border-color: red;");
            valid = false;
        }
        if (branch == null || branch.isEmpty()) {
            FeedBackBranch.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (!valid) {
            FeedBackLabelText.setTextFill(Color.RED);
            FeedBackLabelText.setText("Please fill all fields including branch.");
            FeedBackLabelText.setVisible(true);
            return;
        }

        // Assuming your Feedback entity has a constructor or setter for branch
        Feedback feedback = new Feedback(account, title, details, branch);
        feedback.setBranch(branch); // 👈 assign branch

        try {
            SimpleClient.getClient().sendToServer(feedback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FeedBackLabelText.setTextFill(Color.GREEN);
        FeedBackLabelText.setText("Feedback sent successfully!");
        FeedBackLabelText.setVisible(true);

        // Clear inputs after sending
        FeedBackTitle.clear();
        FeedBackDetails.clear();
        FeedBackBranch.getSelectionModel().clearSelection();
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
        if (!Guest) {
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
        } else {
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

        Flower cartKeyToUpdate = null;
        for (Flower cartFlower : cartMap.keySet()) {
            if (cartFlower.getId() == updatedFlower.getId()) {
                cartKeyToUpdate = cartFlower;
                break;
            }
        }
        if (cartKeyToUpdate != null) {
            int oldQuantity = cartMap.get(cartKeyToUpdate);
            int newSupply = updatedFlower.getSupply();
            int newQuantity = Math.min(oldQuantity, newSupply); // Enforce max quantity = supply

            cartMap.remove(cartKeyToUpdate); // Remove the old key
            cartMap.put(updatedFlower, newQuantity); // Insert updated key with possibly new quantity
            showCart();
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
            if (account.getSubscribtion_level().equals("Plus")) {
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
            if (account.getAuto_renew_subscription().equals("No")) {
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
        Platform.runLater(() -> {
            int flowerID = event.getFlowerID();

            // Find the matching pair before you remove it from cachedFlowerNodes
            Pair<Flower, HBox> toRemove = null;
            for (Pair<Flower, HBox> pair : cachedFlowerNodes) {
                if (pair.getKey().getId() == flowerID) {
                    toRemove = pair;
                    break;
                }
            }

            if (toRemove != null) {
                // Remove the node from the UI
                FlowerPageVbox.getChildren().remove(toRemove.getValue());

                // Remove from cachedFlowerNodes
                cachedFlowerNodes.remove(toRemove);

                // Also remove from cart
                cartMap.entrySet().removeIf(entry -> entry.getKey().getId() == flowerID);
            }
        });
    }

    public void UpdateAccountSubscriptionStatus() {
        SubscribtionLevelLabel.setText(account.getSubscribtion_level() + "user");
        if (account.getSubscribtion_level().equals("Free")) {
            FreeUserLabel.setVisible(true);
            PlusUpgradeButton.setVisible(true);
            CancelRenewButton.setVisible(false);
            PlusUserLabel.setVisible(false);
        } else {
            PlusUserLabel.setVisible(true);
            PlusUserLabel.setText((account.getAuto_renew_subscription().equals("Yes") ? "Renew at " : "Expires at") + account.getSubscription_expires_at());
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
        String supplyHaifaText = NewHaifaFlowerSupply.getText().trim();
        String supplyTelAvivText = NewTelAvivFlowerSupply.getText().trim();
        String supplyEilatText = NewEilatFlowerSupply.getText().trim();
        String supplyStorageText = NewStorageSupply.getText().trim();
        String desc = NewFlowerDesc.getText().trim();

        boolean valid = true;

        if (name.isEmpty() || !name.matches("^[A-Za-z\\s]+$")) {
            NewFlowerName.setStyle("-fx-border-color: red;");
            valid = false;
        } else NewFlowerName.setStyle("");

        if (color.isEmpty() || !color.matches("^[A-Za-z\\s]+$")) {
            NewFlowerColor.setStyle("-fx-border-color: red;");
            valid = false;
        } else NewFlowerColor.setStyle("");

        double price = -1;
        int supplyHaifa = -1, supplyEilat = -1, supplyTelAviv = -1, supplyStorage = -1;

        try {
            price = Double.parseDouble(priceText);
            NewFlowerPrice.setStyle("");
        } catch (NumberFormatException e) {
            NewFlowerPrice.setStyle("-fx-border-color: red;");
            valid = false;
        }

        try {
            supplyHaifa = Integer.parseInt(supplyHaifaText);
            if (supplyHaifa < 0) throw new NumberFormatException();
            NewHaifaFlowerSupply.setStyle("");
        } catch (NumberFormatException e) {
            NewHaifaFlowerSupply.setStyle("-fx-border-color: red;");
            valid = false;
        }

        try {
            supplyTelAviv = Integer.parseInt(supplyTelAvivText);
            if (supplyTelAviv < 0) throw new NumberFormatException();
            NewTelAvivFlowerSupply.setStyle("");
        } catch (NumberFormatException e) {
            NewTelAvivFlowerSupply.setStyle("-fx-border-color: red;");
            valid = false;
        }

        try {
            supplyEilat = Integer.parseInt(supplyEilatText);
            if (supplyEilat < 0) throw new NumberFormatException();
            NewEilatFlowerSupply.setStyle("");
        } catch (NumberFormatException e) {
            NewEilatFlowerSupply.setStyle("-fx-border-color: red;");
            valid = false;
        }

        try {
            supplyStorage = Integer.parseInt(supplyStorageText);
            if (supplyStorage < 0) throw new NumberFormatException();
            NewStorageSupply.setStyle("");
        } catch (NumberFormatException e) {
            NewStorageSupply.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (desc.isEmpty()) {
            NewFlowerDesc.setStyle("-fx-border-color: red;");
            valid = false;
        } else NewFlowerDesc.setStyle("");

        if (!valid) {
            System.err.println("Validation failed.");
            return;
        }

        int totalSupply = supplyHaifa + supplyEilat + supplyTelAviv + supplyStorage;

        Flower newFlower = new Flower(
                name, color, desc, /*imageId*/ "",
                price, totalSupply, supplyHaifa, supplyEilat, supplyTelAviv, supplyStorage
        );


        String suggestedFile = sanitizeBaseName(name) + ".jpg";

        try {
            // Use the extended constructor that carries the JPEG (may be null → server will ignore)
            SimpleClient.getClient().sendToServer(new AddFlowerRequest(newFlower, flowerJpeg, suggestedFile));
            System.out.println("Requested to add new flower: " + name);

            // Clear ALL fields
            NewFlowerName.setText("");
            NewFlowerColor.setText("");
            NewFlowerPrice.setText("");
            NewEilatFlowerSupply.setText("");
            NewTelAvivFlowerSupply.setText("");
            NewHaifaFlowerSupply.setText("");
            NewStorageSupply.setText("");
            NewFlowerDesc.setText("");

            // Reset image drop-zone
            clearFlowerImage();  // should: flowerJpeg=null; flowerImageView.setImage(null); setHintVisible(true); clearImageBtn.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to send add-flower request.\n" + e.getMessage()).showAndWait();
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

    @FXML
    private void openCustomItemWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/CustomItemDialog.fxml"));
            Parent root = fxmlLoader.load();

            CustomItemDialogController dialogController = fxmlLoader.getController();
            dialogController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Create Custom Item");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToCart(Flower flower) {
        cartMap.put(flower, 1); // Add with default quantity 1
        showCart();
        System.out.println("Added custom item to cart: " + flower.getName());
    }

    @Subscribe
    public void onUpdateCreditCardResponse(UpdateCreditCardResponse response) {
        Platform.runLater(() -> {
            if (response.isSuccess()) {
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

    @Subscribe
    public void onPlaceOrderResponse(PlaceOrderResponse response) {
        Platform.runLater(() -> {
            CartMessage.setText(response.getMessage());

            if (response.isSuccess()) {
                CartMessage.setStyle("-fx-text-fill: green;");
            } else {
                CartMessage.setStyle("-fx-text-fill: red;");
            }

            CartMessage.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> CartMessage.setVisible(false));
            pause.play();
        });
    }

    public void showCart() {
        CartVBox.getChildren().clear();
        double totalPrice = 0;
        boolean discountApplied = false;

        for (Map.Entry<Flower, Integer> entry : cartMap.entrySet()) {
            Flower flower = entry.getKey();
            int quantity = entry.getValue();

            // --- Price calculation
            double unitPrice = flower.getDiscount() > 0
                    ? flower.getDiscountPrice()   // discounted price from DB
                    : flower.getPrice();          // normal price

            double price = unitPrice * quantity;
            totalPrice += price;

            // --- UI for cart item
            HBox itemBox = new HBox(10);
            Label nameLabel = new Label(flower.getName() + " x" + quantity);

            if (flower.getDiscount() > 0) {
                // show original price with strikethrough + discounted
                Text orig = new Text("₪" + String.format("%.2f", flower.getPrice() * quantity));
                orig.setStrikethrough(true);
                orig.setFill(javafx.scene.paint.Color.GRAY);

                Label disc = new Label(" → ₪" + String.format("%.2f", price));
                disc.setStyle("-fx-text-fill: #23c55e; -fx-font-weight: bold;");

                HBox priceBox = new HBox(5, orig, disc);
                itemBox.getChildren().addAll(nameLabel, priceBox);
            } else {
                Label priceLabel = new Label("₪" + String.format("%.2f", price));
                itemBox.getChildren().addAll(nameLabel, priceLabel);
            }

            // --- Quantity buttons
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

            itemBox.getChildren().addAll(minusBtn, plusBtn);
            CartVBox.getChildren().add(itemBox);
        }

        // --- Plus member discount (after per-flower discount already applied)
        if (account != null && "Plus".equalsIgnoreCase(account.getSubscribtion_level()) && totalPrice > 50) {
            totalPrice *= 0.9; // Apply 10% discount
            discountApplied = true;
        }

        CartPriceLabel.setText("₪" + String.format("%.2f", totalPrice));
        DiscountLabel.setVisible(discountApplied);
    }


    @Subscribe
    public void updateGrid(List<Account> accounts) {

        Platform.runLater(() -> {
            accountTable.getItems().clear();
            ObservableList<Account> accountList = FXCollections.observableArrayList(accounts);
            accountTable.setItems(accountList);
        });
    }

    private static boolean looksLikeJpg(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".jpg") || n.endsWith(".jpeg");
    }

    private static byte[] prepareJpegPayload(File f) throws IOException {
        if (!looksLikeJpg(f)) throw new IOException("File must be .jpg/.jpeg");
        BufferedImage img = ImageIO.read(f);
        if (img == null) throw new IOException("Invalid image");

        // (Optional) downscale
        final int maxDim = 1600;
        if (img.getWidth() > maxDim || img.getHeight() > maxDim) {
            double s = Math.min((double) maxDim / img.getWidth(), (double) maxDim / img.getHeight());
            int w = (int) Math.round(img.getWidth() * s), h = (int) Math.round(img.getHeight() * s);
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, w, h, null);
            g.dispose();
            img = scaled;
        }

        // Re-encode as JPEG for safety
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter w = it.next();
            w.setOutput(ios);
            ImageWriteParam p = w.getDefaultWriteParam();
            p.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            p.setCompressionQuality(0.85f);
            w.write(null, new IIOImage(img, null, null), p);
            w.dispose();
            byte[] data = baos.toByteArray();
            if (data.length > 4_000_000) throw new IOException("Image too large (>4MB)");
            return data;
        }
    }

    @FXML
    void onGenerateReport(ActionEvent e) {
        // 1) Which report?
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null || reportType.isBlank()) {
            System.err.println("Pick a report type.");
            return;
        }

        // 2) Which branch?
        String lvl = (account != null) ? account.getAccountLevel() : null;

// Force network-wide reports for Branch Managers
        int branchId = 0; // 0 = Network aggregate
        if (!("BranchManager".equalsIgnoreCase(lvl) || "Branch Manager".equalsIgnoreCase(lvl))) {
            String selected = BranchComboBox.getValue();     // e.g. "Haifa Branch (1)"
            branchId = branchNameToId(selected);             // -> 0/1/2/3
        } else {
            String selected = BranchComboBox.getValue();     // e.g. "Haifa Branch (1)"
            branchId = branchNameToId(selected);             // -> 0/1/2/3
        }

        System.out.println("Using branchId = " + branchId + " | report = " + reportType);

        // 3) Dispatch request
        switch (reportType) {
            case "Quarterly Revenue Report" -> requestQuarterlyRevenue(branchId);
            case "Orders by Type Report" -> requestOrdersByType(branchId);
            case "Complaints Report" -> requestComplaints(0);
            default -> System.err.println("Unknown report type: " + reportType);
        }
    }

    private void setClearVisible(boolean v) {
        clearImageBtn.setVisible(v);
        clearImageBtn.setManaged(v);
    }

    private void requestQuarterlyRevenue(int branchId) {
        LocalDate toLD = LocalDate.now();
        LocalDate fromLD = toLD.minusMonths(3);

        Date from = Date.valueOf(fromLD);
        Date to = Date.valueOf(toLD);

        try {
            SimpleClient.getClient().sendToServer(
                    new QuarterlyRevenueReportRequest(from, to, branchId)
            );
            System.out.println("[SEND] QuarterlyRevenueReportRequest(from=" + from + ", to=" + to + ", branchId=" + branchId + ")");
        } catch (IOException ex) {
            System.err.println("[ERROR] QuarterlyRevenueReportRequest failed: " + ex.getMessage());
            new Alert(Alert.AlertType.ERROR, "Could not request Quarterly Revenue Report:\n" + ex.getMessage()).showAndWait();
        }
    }

    private void requestOrdersByType(int branchId) {
        LocalDate toLD = LocalDate.now();
        LocalDate fromLD = toLD.minusMonths(3);

        Date from = Date.valueOf(fromLD);
        Date to = Date.valueOf(toLD);
        try {
            SimpleClient.getClient().sendToServer(new OrdersByProductTypeReportRequest(from, to, branchId));
            System.out.println("[SEND] OrdersByProductTypeReportRequest(branchId=" + branchId + ")");
        } catch (IOException ex) {
            System.err.println("[ERROR] OrdersByProductTypeReportRequest failed: " + ex.getMessage());
            new Alert(Alert.AlertType.ERROR, "Could not request Orders by Type Report:\n" + ex.getMessage()).showAndWait();
        }
    }

    private static String formatCurrency(double value) {
        // Simple IL shekel formatting without locale headaches
        return "₪" + String.format("%,.2f", value);
    }

    // Your existing mapping is fine; keeping here for completeness
    private int branchNameToId(String s) {
        if (s == null) return 0;
        s = s.toLowerCase();
        if (s.startsWith("network")) return 0;
        if (s.startsWith("haifa")) return 1;
        if (s.startsWith("eilat")) return 2;
        if (s.startsWith("tel aviv") || s.startsWith("telaviv")) return 3;
        return 0;
    }

    @org.greenrobot.eventbus.Subscribe
    public void onComplaintsReport(il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsReportResponse resp) {
        javafx.application.Platform.runLater(() -> {
            var rows = (resp != null && resp.getRows() != null) ? resp.getRows() : java.util.List.<il.cshaifasweng.OCSFMediatorExample.entities.ComplaintsReportResponse.Row>of();

            StringBuilder sb = new StringBuilder(2048);
            if (rows.isEmpty()) {
                sb.append("No complaints found for the selected period.");
            } else {
                for (var row : rows) {
                    sb.append("=== ").append(row.getDay()).append(" — Complaints: ").append(row.getCount()).append(" ===\n");
                    var items = row.getDetails();
                    if (items == null || items.isEmpty()) {
                        sb.append("  (No items)\n\n");
                        continue;
                    }
                    for (var it : items) {
                        // Optionally truncate long bodies for readability
                        String body = it.getDetails() == null ? "" : it.getDetails().trim();
                        if (body.length() > 600) body = body.substring(0, 600) + "…";

                        sb.append("• ").append(safe(it.getTitle())).append("  [")
                                .append(it.getStatus()).append("]\n")
                                .append("  From: ").append(safe(it.getEmail()))
                                .append("  | Branch: ").append(safe(it.getBranch()))
                                .append("  | Sent: ").append(String.valueOf(it.getSubmittedAt())).append("\n")
                                .append("  ").append(body).append("\n\n");
                    }
                }
            }

            var ta = new javafx.scene.control.TextArea(sb.toString());
            ta.setEditable(false);
            ta.setWrapText(true);
            ta.setFont(javafx.scene.text.Font.font("monospaced"));
            ta.setPrefSize(900, 600);

            var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Complaints Report — Whole Network");
            alert.setHeaderText("All branches • last 3 months");
            alert.getDialogPane().setContent(ta);
            alert.setResizable(true);
            alert.showAndWait();
        });
    }

    @Subscribe
    public void onQuarterlyRevenueReport(QuarterlyRevenueReportResponse resp) {
        Platform.runLater(() -> {
            var rows = (resp != null && resp.getRows() != null) ? resp.getRows() : List.<QuarterlyRevenueReportResponse.Row>of();

            // Optional: sort by Year, Quarter
            rows.sort(java.util.Comparator
                    .comparingInt(QuarterlyRevenueReportResponse.Row::getYear)
                    .thenComparingInt(QuarterlyRevenueReportResponse.Row::getQuarter));

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-8s | %-8s | %s%n", "Year", "Quarter", "Revenue (₪)"));
            sb.append("------------------------------------------\n");

            double total = 0.0;
            for (var row : rows) {
                total += row.getRevenue();
                sb.append(String.format("%-8d | %-8d | %s%n",
                        row.getYear(),
                        row.getQuarter(),
                        formatCurrency(row.getRevenue())));
            }
            sb.append("------------------------------------------\n");
            sb.append(String.format("%-19s %s%n", "Total:", formatCurrency(total)));

            var textArea = new TextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setFont(Font.font("monospaced"));

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Quarterly Revenue Report");
            a.setHeaderText(rows.isEmpty() ? "No data in range" : "Last 12 months (by quarter)");
            a.getDialogPane().setContent(textArea);
            a.setResizable(true);
            a.showAndWait();
        });
    }

    @Subscribe
    public void onOrdersByProductTypeReport(OrdersByProductTypeReportResponse response) {
        Platform.runLater(() -> {
            var rows = (response != null && response.getRows() != null) ? response.getRows() : List.<OrdersByProductTypeReportResponse.Row>of();

            // Optional: sort by orders desc, then product name
            rows.sort(java.util.Comparator
                    .comparingLong(OrdersByProductTypeReportResponse.Row::getOrders).reversed()
                    .thenComparing(OrdersByProductTypeReportResponse.Row::getProductType, String.CASE_INSENSITIVE_ORDER));

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-24s | %12s | %14s | %14s%n",
                    "Product Type", "Orders", "Total Qty", "Total (₪)"));
            sb.append("--------------------------------------------------------------------------\n");

            long totalOrders = 0;
            long totalQty = 0;
            double totalSum = 0.0;

            if (rows.isEmpty()) {
                sb.append("No order data found for the selected period.");
            } else {
                for (var row : rows) {
                    totalOrders += row.getOrders();
                    totalQty += row.getQuantity();
                    totalSum += row.getTotal();
                    sb.append(String.format("%-24s | %12d | %14d | %14s%n",
                            row.getProductType(),
                            row.getOrders(),
                            row.getQuantity(),
                            formatCurrency(row.getTotal())));
                }
                sb.append("--------------------------------------------------------------------------\n");
                sb.append(String.format("%-24s | %12d | %14d | %14s%n",
                        "TOTAL",
                        totalOrders,
                        totalQty,
                        formatCurrency(totalSum)));
            }

            TextArea textArea = new TextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setFont(Font.font("monospaced"));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Orders by Product Type Report");
            alert.setHeaderText("Report for the last 3 months");
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);
            alert.showAndWait();
        });
    }

    @Subscribe
    public void onGetAllBranchesResponse(GetAllBranchesResponse response) {
        Platform.runLater(() -> {
            List<Branch> branches = response.getBranches();

            // Special "Network" option -> id = 0
            Branch network = new Branch();
            network.setId(0);
            network.setName("Network");

            var items = FXCollections.<Branch>observableArrayList();
            items.add(network);
            items.addAll(branches);
            branchSelectorComboBox.setItems(items);

            // Show: "Haifa Branch (1)" etc.
            branchSelectorComboBox.setConverter(new StringConverter<Branch>() {
                @Override
                public String toString(Branch b) {
                    return (b == null) ? "" : (b.getId() == 0 ? b.getName() : b.getName() + " Branch (" + b.getId() + ")");
                }

                @Override
                public Branch fromString(String s) {
                    return null;
                }
            });
            branchSelectorComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(Branch b, boolean empty) {
                    super.updateItem(b, empty);
                    setText(empty || b == null ? "" : (b.getId() == 0 ? b.getName() : b.getName() + " Branch (" + b.getId() + ")"));
                }
            });

            // Default selection: Network Managers -> Network; Branch Managers -> their own branch
            String lvl = account != null ? account.getAccountLevel() : null;
            if ("NetworkManager".equalsIgnoreCase(lvl)) {
                branchSelectorComboBox.getSelectionModel().select(network);
                branchSelectorComboBox.setDisable(false);
            } else if ("BranchManager".equalsIgnoreCase(lvl) || "Branch Manager".equalsIgnoreCase(lvl)) {
                Branch my = account != null ? account.getBranch() : null;
                if (my != null) {
                    // select the matching object from items (same id)
                    items.stream().filter(b -> b.getId() == my.getId()).findFirst()
                            .ifPresent(b -> branchSelectorComboBox.getSelectionModel().select(b));
                } else {
                    branchSelectorComboBox.getSelectionModel().select(network);
                }
                // Lock selection for branch managers
                branchSelectorComboBox.setDisable(true);
            } else {
                // Other roles: select Network by default
                branchSelectorComboBox.getSelectionModel().select(network);
            }

            // Safety net: keep a selection if items ever refresh
            branchSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
                if (newV == null && !branchSelectorComboBox.getItems().isEmpty()) {
                    branchSelectorComboBox.getSelectionModel().selectFirst();
                }
            });
        });
    }

    private boolean isNetworkManager() {
        return account != null
                && account.getAccountLevel() != null
                && account.getAccountLevel().trim().equalsIgnoreCase("NetworkManager");
    }

    @FXML
    private void onCompareReports(ActionEvent e) {
        // Dates
        LocalDate d1 = (reportDate1 != null) ? reportDate1.getValue() : null;
        LocalDate d2 = (reportDate2 != null) ? reportDate2.getValue() : null;
        if (d1 == null || d2 == null) {
            new Alert(Alert.AlertType.WARNING, "Please pick both dates.").showAndWait();
            return;
        }
        if (d1.equals(d2)) {
            new Alert(Alert.AlertType.WARNING, "Dates must be different.").showAndWait();
            return;
        }
        LocalDate from = d1.isBefore(d2) ? d1 : d2;
        LocalDate to = d1.isBefore(d2) ? d2 : d1;

        int branchId;
        String lvl = (account != null) ? account.getAccountLevel() : null;

        if (lvl != null && (lvl.equalsIgnoreCase("Branch Manager") || lvl.equalsIgnoreCase("BranchManager"))) {
            branchId = 0; // Force Network for Branch Managers
        } else {
            String selectedBranch = (BranchComboBox != null) ? BranchComboBox.getValue() : null;
            branchId = branchNameToId(selectedBranch);
        }

        String reportType = (reportTypeComboBox != null) ? reportTypeComboBox.getValue() : null;
        if (reportType == null || reportType.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please select a report type.").showAndWait();
            return;
        }

        // Send to server (adapt to your messaging)
        try {
            SimpleClient.getClient().sendToServer(
                    new CompareReportsRequest(
                            branchId,
                            reportType, // will be "Compare Reports (by Date)"
                            java.sql.Date.valueOf(from),
                            java.sql.Date.valueOf(to)
                    )
            );
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to send compare request:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }

    }

    private void setCompareControlsVisible(boolean visible) {
        if (reportDate1 != null) {
            reportDate1.setVisible(visible);
            reportDate1.setManaged(visible);
        }
        if (reportDate2 != null) {
            reportDate2.setVisible(visible);
            reportDate2.setManaged(visible);
        }
        if (compareReportsBtn != null) {
            compareReportsBtn.setVisible(visible);
            compareReportsBtn.setManaged(visible);
        }
    }

    @org.greenrobot.eventbus.Subscribe
    public void onCompareReportsResponse(il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse resp) {
        javafx.application.Platform.runLater(() -> {
            // Build a small table with metrics
            javafx.scene.control.TableView<il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse.Row> tv =
                    new javafx.scene.control.TableView<>();

            javafx.scene.control.TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse.Row, String> cMetric =
                    new javafx.scene.control.TableColumn<>("Metric");
            javafx.scene.control.TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse.Row, Number> cA =
                    new javafx.scene.control.TableColumn<>(resp.getDateA().toString());
            javafx.scene.control.TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse.Row, Number> cB =
                    new javafx.scene.control.TableColumn<>(resp.getDateB().toString());
            javafx.scene.control.TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.CompareReportsResponse.Row, Number> cDelta =
                    new javafx.scene.control.TableColumn<>("Δ (B - A)");

            cMetric.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMetric()));
            cA.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getValueA()));
            cB.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getValueB()));
            cDelta.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getDelta()));

            tv.getColumns().addAll(cMetric, cA, cB, cDelta);
            tv.setItems(javafx.collections.FXCollections.observableArrayList(resp.getRows()));

            javafx.scene.control.Dialog<Void> dlg = new javafx.scene.control.Dialog<>();
            dlg.setTitle("Compare Reports");
            dlg.setHeaderText("Branch: " + resp.getBranchName() + " | Type: " + resp.getReportType());
            dlg.getDialogPane().setContent(tv);
            dlg.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
            dlg.showAndWait();
        });
    }

    private void requestComplaints(int branchId) {
        java.time.LocalDate toLD = java.time.LocalDate.now();
        java.time.LocalDate fromLD = toLD.minusMonths(3);

        java.sql.Date from = java.sql.Date.valueOf(fromLD);
        java.sql.Date to = java.sql.Date.valueOf(toLD);

        try {
            SimpleClient.getClient().sendToServer(
                    new ComplaintsReportRequest(from, to, 0) // always network
            );
            System.out.println("[SEND] ComplaintsReportRequest(from=" + from + ", to=" + to + ", branchId=0)");
        } catch (IOException ex) {
            System.err.println("[ERROR] ComplaintsReportRequest failed: " + ex.getMessage());
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Could not request Complaints Report:\n" + ex.getMessage()).showAndWait();
        }
    }

    private void setHintVisible(boolean v) {
        dropHint.setVisible(v);
        dropHint.setManaged(v);
        // mirror the clear button
        clearImageBtn.setVisible(!v);
        clearImageBtn.setManaged(!v); // keep layout clean when hidden
    }

    private void clearFlowerImage() {
        flowerImageView.setImage(null);
        flowerJpeg = null;
        setHintVisible(true); // this hides the clear button too
    }

    @FXML
    void initialize() throws IOException {
        assert AccInfoCCNum != null : "fx:id=\"AccInfoCCNum\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoCCV != null : "fx:id=\"AccInfoCCV\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoCCValidUntil != null : "fx:id=\"AccInfoCCValidUntil\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoEmail != null : "fx:id=\"AccInfoEmail\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoPassword != null : "fx:id=\"AccInfoPassword\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AccInfoPhoneNum != null : "fx:id=\"AccInfoPhoneNum\" was not injected: check your FXML file 'secondary.fxml'.";
        assert AddNewFlower != null : "fx:id=\"AddNewFlower\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CancelRenewButton != null : "fx:id=\"CancelRenewButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartAnchor != null : "fx:id=\"CartAnchor\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartMessage != null : "fx:id=\"CartMessage\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartPriceLabel != null : "fx:id=\"CartPriceLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartTab != null : "fx:id=\"CartTab\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CartVBox != null : "fx:id=\"CartVBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CloseBtn != null : "fx:id=\"CloseBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ContinueToBuyButton != null : "fx:id=\"ContinueToBuyButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CustomTitleBar != null : "fx:id=\"CustomTitleBar\" was not injected: check your FXML file 'secondary.fxml'.";
        assert CustomerServicePanel != null : "fx:id=\"CustomerServicePanel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PaneNewFlower != null : "fx:id=\"PaneNewFlower\" was not injected: check your FXML file 'secondary.fxml'.";
        assert DiscountLabel != null : "fx:id=\"DiscountLabel\" was not injected: check your FXML file 'secondary.fxml'.";
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
        assert NewPassError != null : "fx:id=\"NewPassError\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusLabelPayment != null : "fx:id=\"PlusLabelPayment\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUpgradeButton != null : "fx:id=\"PlusUpgradeButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert PlusUserLabel != null : "fx:id=\"PlusUserLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileSayHelloLabel != null : "fx:id=\"ProfileSayHelloLabel\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileTabConfirmPassText != null : "fx:id=\"ProfileTabConfirmPassText\" was not injected: check your FXML file 'secondary.fxml'.";
        assert ProfileTabNewConfirmPassText != null : "fx:id=\"ProfileTabNewConfirmPassText\" was not injected: check your FXML file 'secondary.fxml'.";
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
        assert ManagerBranch != null : "fx:id=\"ManagerBranch\" was not injected: check your FXML file 'secondary.fxml'.";
        assert imageDropZone != null : "fx:id=\"imageDropZone\" was not injected: check your FXML file 'secondary.fxml'.";
        assert flowerImageView != null : "fx:id=\"flowerImageView\" was not injected: check your FXML file 'secondary.fxml'.";
        assert dropHint != null : "fx:id=\"dropHint\" was not injected: check your FXML file 'secondary.fxml'.";
        assert clearImageBtn != null : "fx:id=\"clearImageBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpgradingAccountError != null : "fx:id=\"UpgradingAccountError\" was not injected: check your FXML file 'secondary.fxml'.";
        FeedBackBranch.setItems(FXCollections.observableArrayList("Haifa", "Tel Aviv", "Eilat"));

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
        ManagerTabs = new Tab[]{
                ManagerPanel,
                CustomerServicePanel,
                detailsChange
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
            } else if (oldTab == FlowersTab) {
                System.out.println("Removing flowers");
                FlowerPageVbox.getChildren().clear();
                cachedFlowerNodes.clear();
            }
        });

        System.out.println("[SecondaryController] Initialized");

        // details change.

        SimpleClient.getClient().sendToServer("GetAccounts");
        accountTable.setEditable(true);
        this.name.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getFirstName() + " " + ((Account) cellData.getValue()).getLastName());
        });
        this.creditCard.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getCreditCardNumber());
        });
        this.id.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getIdentityNumber());
        });
        this.email.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getEmail());
        });
        this.phone.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getPhoneNumber());
        });
        this.password.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getPassword());
        });
        this.sub.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getSubscribtion_level());
        });
        this.accountLevel.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(((Account) cellData.getValue()).getAccountLevel());
        });
        this.branchID.setCellValueFactory((cellData) -> {
            Branch b = ((Account) cellData.getValue()).getBranch();
            int id = 0;
            if (b != null)
                id = b.getId();

            return new SimpleStringProperty("" + id);
        });
        this.name.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.name.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newName = (String) event.getNewValue();
            if (!newName.equals(account.getFirstName() + account.getLastName())) {
                String firstName = newName.split(" ")[0];
                account.setFirstName(firstName);

                String lastName = newName.split(" ")[1];
                account.setLastName(lastName);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // we need to send to the server to update.
            }
        });

        BranchComboBox.getItems().addAll(
                "Network",
                "Haifa Branch (1)",
                "Eilat Branch (2)",
                "Tel Aviv Branch (3)"
        );

        BranchComboBox.setValue("Network");

        this.sub.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.sub.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newSub = (String) event.getNewValue();
            if (!newSub.equals(account.getSubscribtion_level())) {
                account.setSubscribtion_level(newSub);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.id.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.id.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newId = (String) event.getNewValue();
            if (!newId.equals(account.getIdentityNumber())) {
                account.setIdentityNumber(newId);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.phone.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.phone.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newPhone = (String) event.getNewValue();
            if (!newPhone.equals(account.getPhoneNumber())) {
                account.setPhoneNumber(newPhone);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.email.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.email.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newEmail = (String) event.getNewValue();
            if (!newEmail.equals(account.getEmail())) {
                account.setEmail(newEmail);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.password.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.password.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newPassword = (String) event.getNewValue();
            if (!newPassword.equals(account.getPassword())) {
                account.setPassword(newPassword);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.password.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.password.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newPassword = (String) event.getNewValue();
            if (!newPassword.equals(account.getPassword())) {
                account.setPassword(newPassword);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.creditCard.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.creditCard.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String newCreditCard = (String) event.getNewValue();
            if (!newCreditCard.equals(account.getCreditCardNumber())) {
                account.setCreditCardNumber(newCreditCard);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.accountLevel.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.accountLevel.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            String accountLevel = (String) event.getNewValue();
            if (!accountLevel.equals(account.getAccountLevel())) {
                account.setAccountLevel(accountLevel);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.branchID.setCellFactory((col) -> {
            return new TextFieldTableCell(new DefaultStringConverter());
        });
        this.branchID.setOnEditCommit((event) -> {
            Account account = (Account) event.getRowValue();
            int branchID = Integer.parseInt((String) event.getNewValue());
            Branch b = account.getBranch();
            if (b == null) {
                b = new Branch();
                b.setId(-1);
                account.setBranch(b);
            }
            if (!(branchID == b.getId())) {
                b.setId(branchID);
                try {
                    SimpleClient.getClient().sendToServer(account);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Setup for the report generator
        ObservableList<String> reportTypes = FXCollections.observableArrayList(
                "Quarterly Revenue Report",
                "Orders by Type Report",
                "Complaints Report",
                "Compare Reports (by Date)"  // ← now always present
        );
        reportTypeComboBox.setItems(reportTypes);
        reportTypeComboBox.setValue("Quarterly Revenue Report");

        // Hide compare controls initially
        setCompareControlsVisible(false);

        // Toggle UI when report type changes:
        reportTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean compareSelected = "Compare Reports (by Date)".equals(newVal);
            setCompareControlsVisible(compareSelected);         // ← no role check here
            if (generateReportBtn != null) {
                generateReportBtn.setDisable(compareSelected);  // disable Generate while in compare mode
            }
        });

        clearImageBtn.setOnAction(e -> clearFlowerImage());

        imageDropZone.setOnDragOver(e -> {
            if (e.getGestureSource() != imageDropZone
                    && e.getDragboard().hasFiles()
                    && looksLikeJpg(e.getDragboard().getFiles().get(0))) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        imageDropZone.setOnDragEntered(e -> imageDropZone.setStyle("-fx-effect: dropshadow(gaussian, -fx-accent, 10, 0.3, 0, 0);"));
        imageDropZone.setOnDragExited(e -> imageDropZone.setStyle(null));
        imageDropZone.setOnDragDropped(e -> {
            var db = e.getDragboard();
            boolean ok = false;
            if (db.hasFiles()) {
                try {
                    File f = db.getFiles().get(0);
                    flowerJpeg = prepareJpegPayload(f);
                    flowerImageView.setImage(new Image(new ByteArrayInputStream(flowerJpeg)));
                    setHintVisible(false);   // ← shows the clear button
                    ok = true;
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Only JPG allowed.\n" + ex.getMessage()).showAndWait();
                    flowerJpeg = null;
                    flowerImageView.setImage(null);
                    setHintVisible(true);    // ← hides the clear button, shows hint
                }
            }
            e.setDropCompleted(ok);
            e.consume();
        });
        imageDropZone.setOnDragEntered(e ->
                imageDropZone.setEffect(new DropShadow(10, Color.DODGERBLUE))
        );

        imageDropZone.setOnDragExited(e ->
                imageDropZone.setEffect(null)
        );

        App.notifySecondaryReady();
        // 1) add type only for NM
        if (isNetworkManager() && !reportTypeComboBox.getItems().contains("Compare Reports (by Date)")) {
            reportTypeComboBox.getItems().add("Compare Reports (by Date)");
        }

        // 2) show the button (and hide for non-NM)
    }

}
