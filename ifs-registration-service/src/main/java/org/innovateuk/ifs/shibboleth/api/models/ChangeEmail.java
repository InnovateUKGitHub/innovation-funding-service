package org.innovateuk.ifs.shibboleth.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeEmail {

    @Email(message = "{org.innovateuk.ifs.shibboleth.api.models.Identity.email.valid}")
    @JsonProperty(required = true)
    private String email;

}
