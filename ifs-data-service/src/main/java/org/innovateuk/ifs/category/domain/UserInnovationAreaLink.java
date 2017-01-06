package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

@Entity
@DiscriminatorValue("org.innovateuk.ifs.user.domain.User")
public class UserInnovationAreaLink extends CategoryLink<User> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private User user;

    UserInnovationAreaLink() {
        // default constructor
    }

    public UserInnovationAreaLink(User user, Category innovationArea) {
        super(innovationArea);
        if (user == null) {
            throw new NullPointerException("user cannot be null");
        }
        this.user = user;
    }

    @Override
    public User getEntity() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserInnovationAreaLink that = (UserInnovationAreaLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(user, that.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(user)
                .toHashCode();
    }
}