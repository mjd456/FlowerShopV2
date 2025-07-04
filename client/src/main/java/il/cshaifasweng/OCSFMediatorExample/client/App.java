package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private SimpleClient client;
    private static Runnable onSecondaryReady;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
    	EventBus.getDefault().register(this);
        scene = new Scene(loadFXML("ConnectServer"));
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        primaryStage = stage;
        primaryStage.setTitle("Set Connection");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
        client.sendToServer("remove client");
        client.closeConnection();
		super.stop();
	}
    
    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }

    public static void setOnSecondaryReady(Runnable r) {
        onSecondaryReady = r;
    }

    public static void notifySecondaryReady() {
        if (onSecondaryReady != null) {
            Platform.runLater(onSecondaryReady); // ensure safe thread
            onSecondaryReady = null; // one-time
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    @org.greenrobot.eventbus.Subscribe
    public void onTurnUpdate(ConnectToServerEvent event) {
        Platform.runLater(() -> {
            try {
                SimpleClient.getClient().sendToServer("add client");
                client = event.getClientId();
                client.openConnection();

                scene = new Scene(loadFXML("primary"));
                scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.show();

            } catch (IOException e) {
                client = null;
                e.printStackTrace();
                showConnectionError("Failed to connect to the server:\n" + e.getMessage());
            }
        });
    }

    private void showConnectionError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Could not connect to the server");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

}