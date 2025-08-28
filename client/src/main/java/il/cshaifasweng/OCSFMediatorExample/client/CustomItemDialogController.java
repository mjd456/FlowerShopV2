package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
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
    private TextField ColorField;

    @FXML
    private void handleConfirm() {
        String type = ItemTypeField.getText();
        String priceRange = PriceRangeField.getText();
        String color = ColorField.getText();

        if (type == null || type.isEmpty() || priceRange == null || priceRange.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all required fields.");

            // Style with dark theme
            DialogPane dialogPane = alert.getDialogPane();
            URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Could not find dark-theme.css!");
            }

            alert.showAndWait();
            return;
        }

        double price = 0;

        try {
            String[] parts = priceRange.split("-");
            if (parts.length == 2) {
                double min = Double.parseDouble(parts[0].trim());
                double max = Double.parseDouble(parts[1].trim());
                price = (min + max) / 2;
            } else {
                price = Double.parseDouble(priceRange.trim());
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Price");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid price");

            DialogPane dialogPane = alert.getDialogPane();
            URL cssUrl = getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/dark-theme.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Could not find dark-theme.css!");
            }

            alert.showAndWait();
            return;
        }

        Flower customFlower = new Flower(
                type,
                color,
                "Custom item with user details. Price range: " + priceRange,
                "",
                price,
                1,
                0,
                0,
                0,
                0
        );

        if (mainController != null) {
            mainController.addToCart(customFlower);
        }

        Stage stage = (Stage) ItemTypeField.getScene().getWindow();
        stage.close();
    }
}
