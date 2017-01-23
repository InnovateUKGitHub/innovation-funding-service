package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;
import java.util.Set;

/**
 * Abstract DTO for fields common to assessor invite resources.
 */
abstract class AssessorInviteResource {

    private String name;
    private List<InnovationAreaResource> innovationAreas;
    private boolean compliant;


    protected AssessorInviteResource() {
    }

    protected AssessorInviteResource(String name, List<InnovationAreaResource> innovationAreas, boolean compliant) {
        this.name = name;
        this.innovationAreas = innovationAreas;
        this.compliant = compliant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorInviteResource that = (AssessorInviteResource) o;

        return new EqualsBuilder()
                .append(compliant, that.compliant)
                .append(name, that.name)
                .append(innovationAreas, that.innovationAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(innovationAreas)
                .append(compliant)
                .toHashCode();
    }
}