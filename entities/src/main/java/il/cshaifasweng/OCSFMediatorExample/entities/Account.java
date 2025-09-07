package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "identity_number", nullable = false)
    private String identityNumber;

    @Column(name = "credit_card_number", nullable = false)
    private String creditCardNumber;

    @Column(nullable = false)
    private String cvv;

    @Column(name = "credit_card_valid_until", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date creditCardValidUntil;

    @Column(name = "account_level")
    private String accountLevel;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "Logged")
    private boolean logged;

    @Column(name = "subscription")
    private String Subscribtion_level;

    @Column(name = "subscription_expires_at")
    private String Subscription_expires_at;

    @Column(name = "auto_renew_subscription")
    private String Auto_renew_subscription;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_accounts_branch"))
    private Branch branch;
    // ----- Constructors -----

    public Account() {
        this.logged = false;
        this.Subscribtion_level = "Free";
    }

    public Account(String email, String password, String firstName, String lastName,
                   String identityNumber, String creditCardNumber, String cvv, Date creditCardValidUntil,String accountLevel,String phoneNumber) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityNumber = identityNumber;
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.creditCardValidUntil = creditCardValidUntil;
        this.accountLevel = accountLevel;
        this.phoneNumber = phoneNumber;
        this.logged = false;
        this.Subscribtion_level = "Free";
    }

    // ----- Getters and Setters -----

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Date getCreditCardValidUntil() {
        return creditCardValidUntil;
    }

    public void setCreditCardValidUntil(Date creditCardValidUntil) {
        this.creditCardValidUntil = creditCardValidUntil;
    }

    public String getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(String accountLevel) {
        this.accountLevel = accountLevel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public String getSubscribtion_level() {
        return Subscribtion_level;
    }

    public void setSubscribtion_level(String Subscribtion_level) {
        this.Subscribtion_level = Subscribtion_level;
    }

    public String getSubscription_expires_at() {
        return Subscription_expires_at;
    }

    public void setSubscription_expires_at(String Subscription_expires_at) {
        this.Subscription_expires_at = Subscription_expires_at;
    }

    public String getAuto_renew_subscription() {
        return Auto_renew_subscription;
    }

    public void setAuto_renew_subscription(String Auto_renew_subscription) {
        this.Auto_renew_subscription = Auto_renew_subscription;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
