package org.innovateuk.ifs.userorganisation.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserOrganisationPK  implements Serializable{
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "organisation_id")
    private Long organisationId;

    public UserOrganisationPK() {
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }
}