package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.domain.Profile;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Links a {@link Profile} to an {@link InnovationArea}.
 */
@Entity
@DiscriminatorValue("org.innovateuk.ifs.user.domain.Profile")
public class ProfileInnovationAreaLink extends CategoryLink<Profile, InnovationArea> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Profile profile;

    ProfileInnovationAreaLink() {
    }

    public ProfileInnovationAreaLink(Profile profile, InnovationArea innovationArea) {
        super(innovationArea);
        if (profile == null) {
            throw new NullPointerException("profile cannot be null");
        }
        this.profile = profile;
    }

    @Override
    public Profile getEntity() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileInnovationAreaLink that = (ProfileInnovationAreaLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(profile.getId(), that.profile.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(profile.getId())
                .toHashCode();
    }
}
