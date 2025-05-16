package il.cshaifasweng.OCSFMediatorExample.client;

import com.sun.source.tree.TryTree;
import il.cshaifasweng.OCSFMediatorExample.entities.Account;
import il.cshaifasweng.OCSFMediatorExample.entities.LoginResponse;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import java.io.IOException;


public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private static Account account = null;

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
				try {
					App.setRoot("secondary");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				EventBus.getDefault().post(new ErrorMessageEvent("Login failed"));
			}
			account = loginResponse.getAccount();
		} else if (msg instanceof String message) {
			if (message.startsWith("Email already exists")) {
				EventBus.getDefault().post(new ErrorMessageSignUpEvent(message));
			}
		} else {
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
