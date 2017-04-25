package org.innovateuk.ifs.shibboleth.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ChangePassword {

    /**
     * \p{Lu} - Unicode Upper Case
     * \p{Ll} - Unicode Lower Case
     * \p{N}  - Unicode Digit
     * ^.{0,30}$ - Between 0 and 30 characters
     */
    @JsonProperty(required = true)
    @NotBlank(message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.blank}")
    @Size(min = 10, message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.min}")
    @Pattern.List({
        @Pattern(regexp = ".*\\p{N}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.number}"),
        @Pattern(regexp = ".*\\p{Lu}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.uppercase}"),
        @Pattern(regexp = ".*\\p{Ll}+.*", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.complexity.lowercase}"),
        @Pattern(regexp = "^.{0,30}$", message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.password.max}")
    })
    private String password;


    public ChangePassword() {
    }


    // Visible for tests
    public ChangePassword(final String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "ChangePassword{password='[REDACTED]']";
    }
}
