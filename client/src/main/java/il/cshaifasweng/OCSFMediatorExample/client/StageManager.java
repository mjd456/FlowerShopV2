package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {

    public static void replaceScene(String fxmlFile, String title) throws IOException {
        Platform.runLater(() -> {
            try {
                // Load the new FXML
                FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlFile + ".fxml"));
                Parent root = loader.load();

                // Create a new scene
                Scene scene = new Scene(root);

                // Create a new stage
                Stage newStage = new Stage();
                newStage.setTitle(title);
                newStage.setScene(scene);
                newStage.show();

                // Close the current stage
                App.getPrimaryStage().close();

                // Update the reference to the primary stage
                App.setPrimaryStage(newStage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
