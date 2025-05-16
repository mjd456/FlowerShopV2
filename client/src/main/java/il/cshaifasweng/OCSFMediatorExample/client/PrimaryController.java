package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.SignUpRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import il.cshaifasweng.OCSFMediatorExample.entities.LoginRequest;
import org.greenrobot.eventbus.EventBus;


public class PrimaryController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;


	/* ---------- FXML-injected fields ---------- */

	// Login-screen nodes
	@FXML private Label  ErrorMessageLabel, PasswordLabel, EmailLabel;
	@FXML private TextField  EmailField;
	@FXML private Button LoginBtn, SignupBtn, ContinueGuestBtn;
	@FXML private PasswordField PasswordField;

	// Sign-Up-screen nodes
	@FXML private Button BackBtn, SignUpFinal;
	@FXML private Label  CCLabel, CVV, EmailSignUpLabel, ErrorSignUpLabel,
			FirstNameSignUpLabel, IDSignUPLabel, LastNameSignUpLabel,
			PasswordSignUpLabel, PNumberSignUpLabel, ValidUntilLabel;
	@FXML private TextField CCNumberSignUp, CVVNumber, EmailSignUpField,
			FirstNameField, IDField, LastNameField,
			 PhoneField;

	@FXML private DatePicker CCValidDate;
	@FXML private PasswordField PasswordSignUpField;
	/* ---------- Arrays that group the nodes ---------- */

	private Label[]     loginLabels;
	private TextField[] loginTextFields;
	private Button[]    loginButtons;

	private Label[]     signUpLabels;
	private TextField[] signUpTextFields;
	private Button[]    signUpButtons;

	/* ---------- Initialization ---------- */

	@FXML
	void CheckSignUp(ActionEvent event) {
		String email = EmailSignUpField.getText().trim();
		String password = PasswordSignUpField.getText().trim();
		String firstName = FirstNameField.getText().trim();
		String lastName = LastNameField.getText().trim();
		String identityNumber = IDField.getText().trim();
		String creditCardNumber = CCNumberSignUp.getText().trim();
		String cvv = CVVNumber.getText().trim();
		LocalDate localDate = CCValidDate.getValue();

		// Validate names (no digits)
		if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
			showError("Names must not contain numbers.");
			return;
		}

		// Validate email format
		if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			showError("Invalid email format.");
			return;
		}

		// Validate CVV
		if (!cvv.matches("\\d{3}")) {
			showError("CVV must be a 3-digit number.");
			return;
		}

		// Validate credit card number
		if (!creditCardNumber.matches("\\d{13,19}")) {
			showError("Invalid Credit card.");
			return;
		}

		// Validate ID number (you can define your own rules here)
		if (!identityNumber.matches("\\d+")) {
			showError("ID must be numeric.");
			return;
		}

		// Validate credit card expiration date
		if (localDate == null || localDate.isBefore(LocalDate.now())) {
			showError("Credit card expiration date is invalid.");
			return;
		}

		// All fields valid, convert date
		Date creditCardValidUntil = java.sql.Date.valueOf(localDate);

		System.out.println("Sending signup request...");
		System.out.println("Client connected? " + SimpleClient.getClient().isConnected());

		try {
			SimpleClient.getClient().sendToServer(new SignUpRequest(
					email, password, firstName, lastName,
					identityNumber, creditCardNumber, cvv,
					creditCardValidUntil
			));
		} catch (IOException e) {
			showError("Failed to send request to server.");
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		ErrorSignUpLabel.setText(message);
		ErrorSignUpLabel.setVisible(true);
	}



	@FXML
	void ContinueAsGuest(ActionEvent event) {

	}


	@FXML
	void Login(ActionEvent event) {
		String email = EmailField.getText().trim();
		String password = PasswordField.getText().trim();

		if (email.isEmpty() && password.isEmpty()) {
			ErrorMessageLabel.setText("Please enter both username and password.");
			ErrorMessageLabel.setVisible(true);
		} else if (email.isEmpty()) {
			ErrorMessageLabel.setText("Email field is empty.");
			ErrorMessageLabel.setVisible(true);
		} else if (password.isEmpty()) {
			ErrorMessageLabel.setText("Password field is empty.");
			ErrorMessageLabel.setVisible(true);
		} else {
			// Clear previous error
			ErrorMessageLabel.setText("");
			ErrorMessageLabel.setVisible(false);

			// Example placeholder:
			System.out.println("Sending login request for: " + email);

			// If you're using EventBus or Client-Server:
			try {
				SimpleClient.getClient().sendToServer(new LoginRequest(email, password));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/* ---------- Helper to toggle visibility & layout ---------- */

	private void setVisibility(Node[] nodes, boolean visible) {
		for (Node n : nodes) {
			n.setVisible(visible);   // paint/no-paint
			n.setManaged(visible);   // include/exclude in layout
		}
	}


	private void showLoginScreen(boolean showLogin) {
		setVisibility(loginLabels,     showLogin);
		setVisibility(loginTextFields, showLogin);
		setVisibility(loginButtons,    showLogin);

		setVisibility(signUpLabels,     !showLogin);
		setVisibility(signUpTextFields, !showLogin);
		setVisibility(signUpButtons,    !showLogin);
	}

	/* ---------- Button handlers wired in the FXML ---------- */

	/** "Already have an account? Log In" */
	@FXML
	void GoToLogin(ActionEvent event) {
		showLoginScreen(true);
		ErrorSignUpLabel.setVisible(false);
		ErrorMessageLabel.setVisible(false);
		PasswordSignUpField.setVisible(false);
		PasswordField.setVisible(true);
		CCValidDate.setVisible(false);
	}

	/** "Sign Up" */
	@FXML
	void SignUp(ActionEvent event) {
		showLoginScreen(false);
		ErrorSignUpLabel.setVisible(false);
		ErrorMessageLabel.setVisible(false);
		PasswordSignUpField.setVisible(true);
		PasswordField.setVisible(false);
		CCValidDate.setVisible(true);
	}

	@org.greenrobot.eventbus.Subscribe
	public void onErrorLogin(ErrorMessageEvent event) {
		javafx.application.Platform.runLater(()-> {
			ErrorMessageLabel.setVisible(true);
			ErrorMessageLabel.setText(event.getMessage());
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onErrorSignUp(ErrorMessageSignUpEvent event) {
		javafx.application.Platform.runLater(()-> {
			ErrorSignUpLabel.setVisible(true);
			ErrorSignUpLabel.setText(event.getMessage());

			EmailSignUpField.setText("");
			PasswordSignUpField.setText("");
			FirstNameField.setText("");
			LastNameField.setText("");
			IDField.setText("");
			CCNumberSignUp.setText("");
			CVVNumber.setText("");
		});
	}

	@FXML
	void initialize() {
		assert BackBtn != null : "fx:id=\"BackBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert CCLabel != null : "fx:id=\"CCLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert CCNumberSignUp != null : "fx:id=\"CCNumberSignUp\" was not injected: check your FXML file 'primary.fxml'.";
		assert CCValidDate != null : "fx:id=\"CCValidDate\" was not injected: check your FXML file 'primary.fxml'.";
		assert CVV != null : "fx:id=\"CVV\" was not injected: check your FXML file 'primary.fxml'.";
		assert CVVNumber != null : "fx:id=\"CVVNumber\" was not injected: check your FXML file 'primary.fxml'.";
		assert ContinueGuestBtn != null : "fx:id=\"ContinueGuestBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailSignUpField != null : "fx:id=\"EmailSignUpField\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailSignUpLabel != null : "fx:id=\"EmailSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ErrorMessageLabel != null : "fx:id=\"ErrorMessageLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ErrorSignUpLabel != null : "fx:id=\"ErrorSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert FirstNameField != null : "fx:id=\"FirstNameField\" was not injected: check your FXML file 'primary.fxml'.";
		assert FirstNameSignUpLabel != null : "fx:id=\"FirstNameSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert IDField != null : "fx:id=\"IDField\" was not injected: check your FXML file 'primary.fxml'.";
		assert IDSignUPLabel != null : "fx:id=\"IDSignUPLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert LastNameField != null : "fx:id=\"LastNameField\" was not injected: check your FXML file 'primary.fxml'.";
		assert LastNameSignUpLabel != null : "fx:id=\"LastNameSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert LoginBtn != null : "fx:id=\"LoginBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert PNumberSignUpLabel != null : "fx:id=\"PNumberSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordField != null : "fx:id=\"PasswordField\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordLabel != null : "fx:id=\"PasswordLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordSignUpField != null : "fx:id=\"PasswordSignUpField\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordSignUpLabel != null : "fx:id=\"PasswordSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PhoneField != null : "fx:id=\"PhoneField\" was not injected: check your FXML file 'primary.fxml'.";
		assert SignUpFinal != null : "fx:id=\"SignUpFinal\" was not injected: check your FXML file 'primary.fxml'.";
		assert SignupBtn != null : "fx:id=\"SignupBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailField != null : "fx:id=\"EmailField\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailLabel != null : "fx:id=\"EmailLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ValidUntilLabel != null : "fx:id=\"ValidUntilLabel\" was not injected: check your FXML file 'primary.fxml'.";

		loginLabels     = new Label[]     { PasswordLabel, EmailLabel };
		loginTextFields = new TextField[] { PasswordField,     EmailField };
		loginButtons    = new Button[]    { LoginBtn, SignupBtn, ContinueGuestBtn };

		signUpLabels = new Label[] {
				CCLabel, CVV, EmailSignUpLabel,
				FirstNameSignUpLabel, IDSignUPLabel, LastNameSignUpLabel,
				PasswordSignUpLabel, PNumberSignUpLabel, ValidUntilLabel
		};

		signUpTextFields = new TextField[] {
				CCNumberSignUp, CVVNumber, EmailSignUpField,
				FirstNameField, IDField, LastNameField, PasswordSignUpField, PhoneField
		};

		EventBus.getDefault().register(this);
		signUpButtons = new Button[] { BackBtn, SignUpFinal };
		ErrorSignUpLabel.setVisible(false);
		ErrorMessageLabel.setVisible(false);
		// -- choose which screen is shown first (login in this example) --
		showLoginScreen(true);    // true  -> show login, hide sign-up
	}
}
