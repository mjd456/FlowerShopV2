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


	@FXML
	private Button BackBtn;

	@FXML
	private Label CCLabel;

	@FXML
	private TextField CCNumberSignUp;

	@FXML
	private DatePicker CCValidDate;

	@FXML
	private Label CVV;

	@FXML
	private TextField CVVNumber;

	@FXML
	private Button ContinueGuestBtn;

	@FXML
	private TextField EmailField;

	@FXML
	private Label EmailLabel;

	@FXML
	private TextField EmailSignUpField;

	@FXML
	private Label EmailSignUpLabel;

	@FXML
	private Label ErrorMessageLabel;

	@FXML
	private Label ErrorSignUpLabel;

	@FXML
	private Label ErrorVerifyCode;

	@FXML
	private TextField FirstNameField;

	@FXML
	private Label FirstNameSignUpLabel;

	@FXML
	private Label ForgotPassErrorLabel;

	@FXML
	private Label ForgotPassLabel;

	@FXML
	private TextField ForgotPassTextField;

	@FXML
	private TextField IDField;

	@FXML
	private Label IDSignUPLabel;

	@FXML
	private TextField LastNameField;

	@FXML
	private Label LastNameSignUpLabel;

	@FXML
	private Button LoginBtn;

	@FXML
	private Label NewPasswordError;

	@FXML
	private PasswordField NewPasswordField;

	@FXML
	private Label NewPasswordLabel;

	@FXML
	private Label PNumberSignUpLabel;

	@FXML
	private PasswordField PasswordField;

	@FXML
	private Label PasswordLabel;

	@FXML
	private PasswordField PasswordSignUpField;

	@FXML
	private Label PasswordSignUpLabel;

	@FXML
	private TextField PhoneField;

	@FXML
	private Button SendVerifyCode;

	@FXML
	private Button SetNewPasswordButiton;

	@FXML
	private Button SignUpFinal;

	@FXML
	private Button SignupBtn;

	@FXML
	private Label ValidUntilLabel;

	@FXML
	private Button VerifyCodeButton;

	@FXML
	private Label VerifyCodeLabel;

	@FXML
	private TextField VerifyCodeTextField;

	@FXML
	private PasswordField VerifyNewPasswordField;

	@FXML
	private Label VerifyPasswordLabel;

	@FXML
	private Button ForgotPassLogBtn;

	private String RecoveryMail;

	/* ---------- Arrays that group the nodes ---------- */

	private Label[]     loginLabels;
	private TextField[] loginTextFields;
	private Button[]    loginButtons;

	private Label[]     signUpLabels;
	private TextField[] signUpTextFields;
	private Button[]    signUpButtons;

	private Label[]    verifyCodeLabels;
	private Button[]    verifyCodeButtons;
	private TextField[] verifyCodeTextFields;

	private Label[]    verifyNewPasswordLabels;
	private Button[]    verifyNewPasswordButtons;
	private PasswordField[] verifyNewPasswordTextFields;

	private Label[] forgotPassLabels;
	private Button[]    forgotPassButtons;
	private TextField[] forgotPassTextFields;

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
		String phoneNumber = PhoneField.getText().trim();
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

		// Validate phone number
		if (!phoneNumber.matches("^\\+?\\d{7,15}$")) {
			showError("Invalid phone number.");
			return;
		}


		// All fields valid, convert date
		Date creditCardValidUntil = java.sql.Date.valueOf(localDate);

		try {
			SimpleClient.getClient().sendToServer(new SignUpRequest(
					email, password, firstName, lastName,
					identityNumber, creditCardNumber, cvv,
					creditCardValidUntil,phoneNumber
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

	private void hideAllSections() {
		setVisibility(loginLabels, false);
		setVisibility(loginTextFields, false);
		setVisibility(loginButtons, false);

		setVisibility(signUpLabels, false);
		setVisibility(signUpTextFields, false);
		setVisibility(signUpButtons, false);

		setVisibility(forgotPassLabels, false);
		setVisibility(forgotPassTextFields, false);
		setVisibility(forgotPassButtons, false);

		setVisibility(verifyCodeLabels, false);
		setVisibility(verifyCodeTextFields, false);
		setVisibility(verifyCodeButtons, false);

		setVisibility(verifyNewPasswordLabels, false);
		setVisibility(verifyNewPasswordTextFields, false);
		setVisibility(verifyNewPasswordButtons, false);
	}


	private void showLoginTab() {
		hideAllSections();
		setVisibility(loginLabels, true);
		setVisibility(loginTextFields, true);
		setVisibility(loginButtons, true);
		emptyFields(loginTextFields);
	}

	private void showSignUpTab() {
		hideAllSections();
		setVisibility(signUpLabels, true);
		setVisibility(signUpTextFields, true);
		setVisibility(signUpButtons, true);
		emptyFields(signUpTextFields);
	}

	private void showForgotPasswordTab() {
		hideAllSections();
		setVisibility(forgotPassLabels, true);
		setVisibility(forgotPassTextFields, true);
		setVisibility(forgotPassButtons, true);
		BackBtn.setVisible(true);
		emptyFields(forgotPassTextFields);
	}


	private void showVerifyCodeTab() {
		hideAllSections();
		setVisibility(verifyCodeLabels, true);
		setVisibility(verifyCodeTextFields, true);
		setVisibility(verifyCodeButtons, true);
		ErrorVerifyCode.setVisible(false);
		BackBtn.setVisible(true);
		emptyFields(verifyCodeTextFields);
	}


	private void showSetNewPasswordTab() {
		hideAllSections();
		setVisibility(verifyNewPasswordLabels, true);
		setVisibility(verifyNewPasswordTextFields, true);
		setVisibility(verifyNewPasswordButtons, true);
		NewPasswordError.setVisible(false);
		BackBtn.setVisible(true);
		emptyFields(verifyNewPasswordTextFields);
	}

	private void emptyFields(TextField[] fields) {
		for (TextField field : fields) {
			field.setText("");
		}
	}

	private void showLoginScreen(boolean showLogin) {
		if (showLogin) {
			showLoginTab();
		} else {
			showSignUpTab();
		}
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
		BackBtn.setVisible(false);
		CCValidDate.setVisible(false);
	}

	@FXML
	void SwitchToForgotPass(ActionEvent event) {
		showForgotPasswordTab();
		ForgotPassErrorLabel.setVisible(false);
		BackBtn.setVisible(true);
	}

	@FXML
	void SendToEmailCode(ActionEvent event) {
		//check with server if email exist , if so send the code
		if(!ForgotPassTextField.getText().isEmpty()) {
			try {
				SimpleClient.getClient().sendToServer("Does this email exist? " + ForgotPassTextField.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ForgotPassErrorLabel.setVisible(true);
			ForgotPassErrorLabel.setText("Email field is empty.");
		}
	}

	@FXML
	void VerifyCode(ActionEvent event) {
		//send to server the code to check then server tell you to go to setpass tab
		if(!VerifyCodeTextField.getText().isEmpty()) {
			try {
				ErrorVerifyCode.setVisible(false);
				SimpleClient.getClient().sendToServer("Is the code correct? " + VerifyCodeTextField.getText() + " " + RecoveryMail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			ErrorVerifyCode.setText("Code field is empty.");
			ErrorVerifyCode.setVisible(true);
		}
	}

	@FXML
	void SetPassword(ActionEvent event) {
		//check with server if pass is not used , if not then set it and go to login
		if(NewPasswordField.getText().equals(VerifyNewPasswordField.getText())) {
			if(!NewPasswordField.getText().isEmpty()) {
				try {
					NewPasswordError.setVisible(false);
					SimpleClient.getClient().sendToServer("Is this password unique? " + NewPasswordField.getText() + " " + RecoveryMail);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				NewPasswordError.setVisible(true);
				NewPasswordError.setText("Password field is empty.");
			}
		} else {
			NewPasswordError.setVisible(true);
			NewPasswordError.setText("Password fields need to be identical.");
		}
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
			PhoneField.setText("");
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onSignUpSuccess(SignUpSuccess event) {
		javafx.application.Platform.runLater(()-> {
			showLoginScreen(true);
			ErrorSignUpLabel.setVisible(false);
			ErrorMessageLabel.setVisible(false);
			PasswordSignUpField.setVisible(false);
			PasswordField.setVisible(true);
			CCValidDate.setVisible(false);
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onRecoveryMailNotFound(RecoveryMailNotFound event) {
		javafx.application.Platform.runLater(()-> {
			ForgotPassErrorLabel.setText(event.getToken());
			ForgotPassErrorLabel.setVisible(true);
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onRecoveryMailFound(RecoveryMailFound event) {
		javafx.application.Platform.runLater(()-> {
			RecoveryMail = event.getEmailToCheck();
			showVerifyCodeTab();
			NewPasswordError.setVisible(false);
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onVerifyCodeError(VerifyCodeError event) {
		javafx.application.Platform.runLater(()-> {
			ErrorVerifyCode.setVisible(true);
			ErrorVerifyCode.setText(event.getError());
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onVerifiedCodeForRecovery(VerifiedCodeForRecovery event) {
		javafx.application.Platform.runLater(()-> {
			showSetNewPasswordTab();
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onChangingPasswordError(ChangingPasswordError event) {
		javafx.application.Platform.runLater(()-> {
			NewPasswordError.setVisible(true);
			NewPasswordError.setText(event.getErrorMessage());
		});
	}


	@org.greenrobot.eventbus.Subscribe
	public void onChangingPasswordSuccess(ChangingPasswordSuccess event) {
		javafx.application.Platform.runLater(()-> {
			showLoginScreen(true);
			ErrorSignUpLabel.setVisible(false);
			ErrorMessageLabel.setVisible(false);
			PasswordSignUpField.setVisible(false);
			PasswordField.setVisible(true);
			BackBtn.setVisible(false);
			CCValidDate.setVisible(false);
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
		assert EmailField != null : "fx:id=\"EmailField\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailLabel != null : "fx:id=\"EmailLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailSignUpField != null : "fx:id=\"EmailSignUpField\" was not injected: check your FXML file 'primary.fxml'.";
		assert EmailSignUpLabel != null : "fx:id=\"EmailSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ErrorMessageLabel != null : "fx:id=\"ErrorMessageLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ErrorSignUpLabel != null : "fx:id=\"ErrorSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ErrorVerifyCode != null : "fx:id=\"ErrorVerifyCode\" was not injected: check your FXML file 'primary.fxml'.";
		assert FirstNameField != null : "fx:id=\"FirstNameField\" was not injected: check your FXML file 'primary.fxml'.";
		assert FirstNameSignUpLabel != null : "fx:id=\"FirstNameSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ForgotPassErrorLabel != null : "fx:id=\"ForgotPassErrorLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ForgotPassLabel != null : "fx:id=\"ForgotPassLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert ForgotPassLogBtn != null : "fx:id=\"ForgotPassLogBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert ForgotPassTextField != null : "fx:id=\"ForgotPassTextField\" was not injected: check your FXML file 'primary.fxml'.";
		assert IDField != null : "fx:id=\"IDField\" was not injected: check your FXML file 'primary.fxml'.";
		assert IDSignUPLabel != null : "fx:id=\"IDSignUPLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert LastNameField != null : "fx:id=\"LastNameField\" was not injected: check your FXML file 'primary.fxml'.";
		assert LastNameSignUpLabel != null : "fx:id=\"LastNameSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert LoginBtn != null : "fx:id=\"LoginBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert NewPasswordError != null : "fx:id=\"NewPasswordError\" was not injected: check your FXML file 'primary.fxml'.";
		assert NewPasswordField != null : "fx:id=\"NewPasswordField\" was not injected: check your FXML file 'primary.fxml'.";
		assert NewPasswordLabel != null : "fx:id=\"NewPasswordLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PNumberSignUpLabel != null : "fx:id=\"PNumberSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordField != null : "fx:id=\"PasswordField\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordLabel != null : "fx:id=\"PasswordLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordSignUpField != null : "fx:id=\"PasswordSignUpField\" was not injected: check your FXML file 'primary.fxml'.";
		assert PasswordSignUpLabel != null : "fx:id=\"PasswordSignUpLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert PhoneField != null : "fx:id=\"PhoneField\" was not injected: check your FXML file 'primary.fxml'.";
		assert SendVerifyCode != null : "fx:id=\"SendVerifyCode\" was not injected: check your FXML file 'primary.fxml'.";
		assert SetNewPasswordButiton != null : "fx:id=\"SetNewPasswordButiton\" was not injected: check your FXML file 'primary.fxml'.";
		assert SignUpFinal != null : "fx:id=\"SignUpFinal\" was not injected: check your FXML file 'primary.fxml'.";
		assert SignupBtn != null : "fx:id=\"SignupBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert ValidUntilLabel != null : "fx:id=\"ValidUntilLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert VerifyCodeButton != null : "fx:id=\"VerifyCodeButton\" was not injected: check your FXML file 'primary.fxml'.";
		assert VerifyCodeLabel != null : "fx:id=\"VerifyCodeLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert VerifyCodeTextField != null : "fx:id=\"VerifyCodeTextField\" was not injected: check your FXML file 'primary.fxml'.";
		assert VerifyNewPasswordField != null : "fx:id=\"VerifyNewPasswordField\" was not injected: check your FXML file 'primary.fxml'.";
		assert VerifyPasswordLabel != null : "fx:id=\"VerifyPasswordLabel\" was not injected: check your FXML file 'primary.fxml'.";

		loginLabels     = new Label[]     { PasswordLabel, EmailLabel };
		loginTextFields = new TextField[] { PasswordField,     EmailField };
		loginButtons    = new Button[]    { LoginBtn, SignupBtn, ContinueGuestBtn, ForgotPassLogBtn };

		signUpLabels = new Label[] {
				CCLabel, CVV, EmailSignUpLabel,
				FirstNameSignUpLabel, IDSignUPLabel, LastNameSignUpLabel,
				PasswordSignUpLabel, PNumberSignUpLabel, ValidUntilLabel,ErrorSignUpLabel
		};

		signUpTextFields = new TextField[] {
				CCNumberSignUp, CVVNumber, EmailSignUpField,
				FirstNameField, IDField, LastNameField, PasswordSignUpField, PhoneField
		};

		verifyNewPasswordButtons = new Button[] {
				SetNewPasswordButiton
		};

		verifyNewPasswordTextFields = new PasswordField[] {
				NewPasswordField, VerifyNewPasswordField
		};

		verifyNewPasswordLabels = new Label[] {
				NewPasswordError, NewPasswordLabel, VerifyPasswordLabel
		};

		verifyCodeButtons = new Button[] {
				VerifyCodeButton
		};

		verifyCodeTextFields = new TextField[] {
				VerifyCodeTextField
		};

		verifyCodeLabels = new Label[] {
				VerifyCodeLabel, ErrorVerifyCode
		};

		forgotPassLabels = new Label[] {
				ForgotPassLabel, ForgotPassErrorLabel
		};

		forgotPassTextFields = new TextField[] {
				ForgotPassTextField
		};

		forgotPassButtons = new Button[] {
				SendVerifyCode
		};

		EventBus.getDefault().register(this);
		signUpButtons = new Button[] { BackBtn, SignUpFinal };
		ErrorSignUpLabel.setVisible(false);
		ErrorMessageLabel.setVisible(false);
		// -- choose which screen is shown first (login in this example) --
		showLoginScreen(true);    // true  -> show login, hide sign-up
	}
}
