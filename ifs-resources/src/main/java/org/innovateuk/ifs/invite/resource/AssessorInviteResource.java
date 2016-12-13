package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;

/**
 * Abstract DTO for fields common to assessor invite resources.
 */
abstract class AssessorInviteResource {

    private String firstName;
    private String lastName;
    private CategoryResource innovationArea;
    private boolean compliant;

    protected AssessorInviteResource() {
    }

    protected AssessorInviteResource(String firstName, String lastName, CategoryResource innovationArea, boolean compliant) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.innovationArea = innovationArea;
        this.compliant = compliant;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(innovationArea, that.innovationArea)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(innovationArea)
                .append(compliant)
                .toHashCode();
    }
}