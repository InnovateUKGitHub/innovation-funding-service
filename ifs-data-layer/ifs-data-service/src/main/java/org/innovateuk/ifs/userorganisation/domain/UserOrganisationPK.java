package org.innovateuk.ifs.userorganisation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserOrganisationPK that = (UserOrganisationPK) o;

        return new EqualsBuilder()
                .append(user, that.user)
                .append(organisation, that.organisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(organisation)
                .toHashCode();
    }
}