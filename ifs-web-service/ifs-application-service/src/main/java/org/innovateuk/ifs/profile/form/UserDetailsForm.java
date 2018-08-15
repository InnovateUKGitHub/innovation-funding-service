package org.innovateuk.ifs.profile.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * This object is used for the editing of user details. When the form is submitted the data is
 * injected into a UserDetailsForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data.
 */
public class UserDetailsForm {

    private String email;

    private boolean allowMarketingEmails;

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

    private String actionUrl;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean getAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public void setAllowMarketingEmails(boolean allowMarketingEmails) {
        this.allowMarketingEmails = allowMarketingEmails;
    }
}