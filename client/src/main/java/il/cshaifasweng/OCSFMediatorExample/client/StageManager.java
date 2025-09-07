package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.LogoutRequest;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
                scene.getStylesheets().add(App.class.getResource("dark-theme.css").toExternalForm());
                // Create a new stage
                Stage newStage = new Stage();
                newStage.initStyle(StageStyle.UNDECORATED);
                newStage.setTitle(title);
                newStage.setScene(scene);
                newStage.setResizable(false);
                newStage.show();
                newStage.setOnCloseRequest(event -> {
                    try {
                        if (SimpleClient.account != null) {
                            try {
                                SimpleClient.getClient().sendToServer(new LogoutRequest(SimpleClient.account));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("Sent logout request on window close.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                App.getPrimaryStage().close();
                App.setPrimaryStage(newStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
