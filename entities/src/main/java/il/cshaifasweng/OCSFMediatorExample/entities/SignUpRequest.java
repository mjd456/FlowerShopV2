package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

public class SignUpRequest implements Serializable {
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String identityNumber;

    private String creditCardNumber;

    private String cvv;

    private Date creditCardValidUntil;

    public SignUpRequest(String email, String password,String firstName, String lastName,String identityNumber,
                         String creditCardNumber,String cvv,Date creditCardValidUntil) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityNumber = identityNumber;
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.creditCardValidUntil = creditCardValidUntil;
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
}
