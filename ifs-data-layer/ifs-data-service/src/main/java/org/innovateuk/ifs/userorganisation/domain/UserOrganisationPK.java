package org.innovateuk.ifs.userorganisation.domain;

import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class UserOrganisationPK  implements Serializable{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id" , referencedColumnName = "id")
    private Organisation organisation;

    public UserOrganisationPK() {
    }

    public UserOrganisationPK(User user, Organisation organisation) {
        this.user = user;
        this.organisation = organisation;
    }

    public User getUser() {
        return user;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}