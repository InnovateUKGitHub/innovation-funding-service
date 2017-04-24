package org.innovateuk.ifs.shibboleth.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NewIdentity {

    @JsonProperty(required = true)
    @Email(message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.email.valid}")
    private String email;

    /**
     * \p{N}  - Unicode Digit
     * \p{Lu} - Unicode Upper Case
     * \p{Ll} - Unicode Lower Case
     * ^.{0,30}$ - Between 0 and 30 characters
     */
    @JsonProperty(required = true)
    @NotBlank(message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.blank}")
    @Size(min = 8, message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.min}")
    @Pattern.List({
            @Pattern(regexp = ".*\\p{N}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.number}"),
            @Pattern(regexp = ".*\\p{Lu}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.uppercase}"),
            @Pattern(regexp = ".*\\p{Ll}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.lowercase}"),
            @Pattern(regexp = "^.{0,30}$", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.max}")
    })
    private String password;


    public NewIdentity() {
    }


    // Visible for tests
    public NewIdentity(final String email, final String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "NewIdentity{" +
            "email='" + email + '\'' +
            ", password=''[REDACTED]'" +
            '}';
    }

}
