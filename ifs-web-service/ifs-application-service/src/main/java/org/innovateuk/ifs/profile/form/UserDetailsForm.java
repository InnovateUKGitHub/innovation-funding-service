package org.innovateuk.ifs.profile.form;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.user.resource.EDIStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * This object is used for the editing of user details. When the form is submitted the data is
 * injected into a UserDetailsForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data.
 */
@Getter
@Setter
public class UserDetailsForm {

    private String email;

    private boolean allowMarketingEmails;

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.firstname.length.min}"),
            @Size(max = 70, message = "{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.lastname.length.min}"),
            @Size(max = 70, message = "{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER, message = "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    private String actionUrl;

    private EDIStatus ediStatus;

    private ZonedDateTime lastReviewDate;

}
