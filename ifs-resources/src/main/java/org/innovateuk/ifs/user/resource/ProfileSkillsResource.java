package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Profile Skills Data Transfer Object
 */
public class ProfileSkillsResource extends ProfileSkillsBaseResource {

    private List<InnovationAreaResource> innovationAreas;

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProfileSkillsResource that = (ProfileSkillsResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(innovationAreas, that.innovationAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(innovationAreas)
                .toHashCode();
    }
}
