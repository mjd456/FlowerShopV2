package il.cshaifasweng.OCSFMediatorExample.client;

import com.sun.source.tree.TryTree;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import java.io.IOException;
import java.util.Map;


public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	static Account account = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg instanceof LoginResponse loginResponse) {
			if (loginResponse.isSuccess()) {
				account = loginResponse.getAccount();

				App.setOnSecondaryReady(() -> {
					System.out.println("[Scene ready] Logged in as: " + account.getFirstName());

					EventBus.getDefault().postSticky(new SetAccountLevel(account));

					try {
						SimpleClient.getClient().sendToServer("RefreshList");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				try {
					StageManager.replaceScene("secondary", "Secondary Window");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				EventBus.getDefault().post(new ErrorMessageEvent("Login failed"));
			}
		}
		else if (msg instanceof String message) {
			if (message.startsWith("Email already exists")) {
				EventBus.getDefault().post(new ErrorMessageSignUpEvent(message));
			} else if (message.startsWith("Sign-up succeeded for email")) {
				EventBus.getDefault().post(new SignUpSuccess());
			} else if (message.startsWith("Email for recovery found")) {
				String[] tokens = message.split("\\s+");
				EventBus.getDefault().post(new RecoveryMailFound(tokens[4]));
			} else if (message.startsWith("Email for recovery not found")) {
				EventBus.getDefault().post(new RecoveryMailNotFound("Invalid email address"));
			}else if (message.startsWith("The code has expired. A new code was sent to")) {
				EventBus.getDefault().post(new VerifyCodeError("The code has expired. A new code was sent to your email."));
			} else if (message.startsWith("Invalid verification code.")) {
				EventBus.getDefault().post(new VerifyCodeError("Invalid verification code."));
			} else if (message.startsWith("Code verified successfully.")) {
				EventBus.getDefault().post(new VerifiedCodeForRecovery());
			} else if (message.startsWith("Changing password error : ")) {
				String prefix = "Changing password error :";
				if (message.startsWith(prefix)) {
					String errorMessage = message.substring(prefix.length()).trim();
					System.out.println("Error message: " + errorMessage);
					EventBus.getDefault().post(new ChangingPasswordError(errorMessage));
				}
			} else if(message.startsWith("Password successfully changed.")) {
				EventBus.getDefault().post(new ChangingPasswordSuccess());
			}
		}
		else if (msg instanceof Map<?, ?> map && map.keySet().iterator().next() instanceof Flower) {
			Map<Flower, byte[]> flowerImageMap = (Map<Flower, byte[]>) msg;
			System.out.println("Posting FlowerListEventBus");
			System.out.println("Registered subscribers: " + EventBus.getDefault().hasSubscriberForEvent(FlowerListEventBus.class));
			EventBus.getDefault().postSticky(new FlowerListEventBus(flowerImageMap));
		}
		else {
			System.out.println("Unhandled message type: " + msg.getClass().getSimpleName());
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}
