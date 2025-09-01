package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

public class CustomItemDialogController {

    private SecondaryController mainController;

    public void setMainController(SecondaryController controller) {
        this.mainController = controller;
    }

    @FXML
    private TextField ItemTypeField;

    @FXML
    private TextField PriceRangeField;

    @FXML
    private ComboBox<String> ColorField;

    @FXML
    private void handleConfirm() {
        String type = ColorField.getValue() + " - " + ItemTypeField.getText();
        String priceRange = PriceRangeField.getText();
        String color = ColorField.getValue();

        if (type == null || type.isEmpty() || priceRange == null || priceRange.isEmpty()) {
            showWarn("Missing Information", "Please fill all required fields.");
            return;
        }

        double price;
        try {
            String[] parts = priceRange.split("-");
            if (parts.length == 2) {
                double min = Double.parseDouble(parts[0].trim());
                double max = Double.parseDouble(parts[1].trim());
                price = (min + max) / 2.0;
            } else {
                price = Double.parseDouble(priceRange.trim());
            }
            if (price < 100 || price > 200) throw new NumberFormatException("Price out of allowed range");
        } catch (NumberFormatException e) {
            showWarn("Invalid Price", "Please enter a valid price between 100 and 200.");
            return;
        }

        Flower customFlower = new Flower(
                type,
                color,
                "Custom item with user details. Price range: " + priceRange,
                "",
                price,
                Integer.MAX_VALUE, // treat as “infinite” stock client-side
                0, 0, 0, 0
        );

        // >>> give it a unique temp id so Map<Flower,Integer> treats each as distinct
        // negative id avoids clashing with real DB ids (>0)
        int tempId = (int) -(System.nanoTime() & 0x7FFFFFFF);
        customFlower.setId(tempId);

        if (mainController != null) {
            mainController.addToCart(customFlower);
        }

        ((Stage) ItemTypeField.getScene().getWindow()).close();
    }

    // helper to show styled warning
    private void showWarn(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        DialogPane dp = alert.getDialogPane();
        URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
        if (cssUrl != null) dp.getStylesheets().add(cssUrl.toExternalForm());
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        ColorField.getItems().setAll(
                "Red", "Pink", "White", "Yellow", "Purple", "Blue", "Orange", "Green", "Violet", "Any"
        );
        ColorField.setPromptText("e.g., select a color");

        ColorField.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color:  #2b2f3a;");
                }
            }
        });

// Also style the selected item shown in the ComboBox itself:
        ColorField.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color:  #2b2f3a;");
                }
            }
        });

        ColorField.setValue("Any");
    }
}
