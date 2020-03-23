package org.innovateuk.ifs.registration.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * This object is used for the account registration form. When the form is submitted the data is
 * injected into a RegistrationForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data.
 */

public class RegistrationForm extends BaseBindingResultTarget {

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @NotBlank(message = "{validation.standard.email.required}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    @NotBlank(message = "{validation.standard.password.required}")
    @Size.List ({
        @Size(min=8, message="{validation.standard.password.length.min}"),
    })
    private String password;

    private String title;

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List ({
        @Size(min=2, message="{validation.standard.firstname.length.min}"),
        @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List ({
        @Size(min=2, message="{validation.standard.lastname.length.min}"),
        @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    @NotBlank(message = "{validation.account.termsandconditions.required}")
    private String termsAndConditions;

    private Boolean allowMarketingEmails;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RegistrationForm withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getTitle() {
        return title;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Boolean getAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public void setAllowMarketingEmails(Boolean allowMarketingEmails) {
        this.allowMarketingEmails = allowMarketingEmails;
    }
}
