package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;

/**
 * Abstract DTO for fields common to assessor invite resources.
 */
abstract class AssessorInviteResource {

    private String name;
    private CategoryResource innovationArea;
    private boolean compliant;


    protected AssessorInviteResource() {
    }

    protected AssessorInviteResource(String name, CategoryResource innovationArea, boolean compliant) {
        this.name = name;
        this.innovationArea = innovationArea;
        this.compliant = compliant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryResource getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(CategoryResource innovationArea) {
        this.innovationArea = innovationArea;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }

    public String getInnovationAreaName() {
        return this.getInnovationArea() == null ? null : this.getInnovationArea().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorInviteResource that = (AssessorInviteResource) o;

        return new EqualsBuilder()
                .append(compliant, that.compliant)
                .append(name, that.name)
                .append(innovationArea, that.innovationArea)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(innovationArea)
                .append(compliant)
                .toHashCode();
    }
}