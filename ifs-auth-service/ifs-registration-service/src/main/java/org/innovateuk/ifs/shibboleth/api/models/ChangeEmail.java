package org.innovateuk.ifs.shibboleth.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;

public class ChangeEmail {

    @Email(message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.email.valid}")
    @JsonProperty(required = true)
    private String email;


    public ChangeEmail() {
    }


    // Visible for tests
    public ChangeEmail(final String email) {
        this.email = email;
    }


    public String getEmail() {
        return email;
    }


    @Override
    public String toString() {
        return "ChangeEmail{" +
            "email='" + email + '\'' +
            '}';
    }
}
