package org.innovateuk.ifs.userorganisation.domain;

import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

/**
 * UserOrganisation object for linking user to organisation.
 */
@Entity
public class UserOrganisation {
    @EmbeddedId
    private UserOrganisationPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , referencedColumnName = "id", insertable=false, updatable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id" , referencedColumnName = "id", insertable=false, updatable=false)
    private Organisation organisation;

    public UserOrganisation() {
    }

    public UserOrganisation(UserOrganisationPK id) {
        this.id = id;
    }

    public UserOrganisationPK getId() {
        return id;
    }

    public void setId(UserOrganisationPK id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}