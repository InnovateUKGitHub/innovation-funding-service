package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;

/**
 * DTO for a created assessor invite that is ready to be sent.
 */
public class AssessorCreatedInviteResource extends AssessorInviteResource {

    private String email;

    public AssessorCreatedInviteResource() {
    }

    public AssessorCreatedInviteResource(String firstName, String lastName, CategoryResource innovationArea, boolean compliant, String email) {
        super(firstName, lastName, innovationArea, compliant);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorCreatedInviteResource that = (AssessorCreatedInviteResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(email)
                .toHashCode();
    }
}