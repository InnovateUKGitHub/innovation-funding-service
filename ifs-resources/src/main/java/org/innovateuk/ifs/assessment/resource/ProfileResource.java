package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for a User's {@code Profile}.
 */
public class ProfileResource {

    private List<InnovationAreaResource> innovationAreas = new ArrayList<>();
    private BusinessType businessType;
    private String skillsAreas;
    private List<AffiliationResource> affiliations = new ArrayList<>();
    private AddressResource address;

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getSkillsAreas() {
        return skillsAreas;
    }

    public void setSkillsAreas(String skills) {
        this.skillsAreas = skills;
    }

    public List<AffiliationResource> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationResource> affiliations) {
        this.affiliations = affiliations;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProfileResource that = (ProfileResource) o;

        return new EqualsBuilder()
                .append(innovationAreas, that.innovationAreas)
                .append(businessType, that.businessType)
                .append(skillsAreas, that.skillsAreas)
                .append(affiliations, that.affiliations)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(innovationAreas)
                .append(businessType)
                .append(skillsAreas)
                .append(affiliations)
                .append(address)
                .toHashCode();
    }
}
