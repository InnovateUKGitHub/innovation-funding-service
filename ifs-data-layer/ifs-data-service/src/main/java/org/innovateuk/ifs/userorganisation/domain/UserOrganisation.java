package org.innovateuk.ifs.userorganisation.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * UserOrganisation object for linking user to organisation.
 */
@Entity
public class UserOrganisation {
    @EmbeddedId
    private UserOrganisationPK id;

    public UserOrganisation() {
    }

    public UserOrganisationPK getId() {
        return id;
    }
}