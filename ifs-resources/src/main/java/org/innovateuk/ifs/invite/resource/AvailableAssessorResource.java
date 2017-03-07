package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * DTO for an assessor that is available to be invited.
 */
public class AvailableAssessorResource extends AssessorInviteResource {

    private String email;
    private BusinessType businessType;

    public AvailableAssessorResource() {
    }

    public AvailableAssessorResource(Long id,
                                     String name,
                                     List<InnovationAreaResource> innovationAreas,
                                     boolean compliant,
                                     String email,
                                     BusinessType businessType) {
        super(id, name, innovationAreas, compliant);
        this.email = email;
        this.businessType = businessType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AvailableAssessorResource that = (AvailableAssessorResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(email, that.email)
                .append(businessType, that.businessType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(email)
                .append(businessType)
                .toHashCode();
    }
}
