package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to encapsulate an Assessors profile view.
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
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileResource that = (ProfileResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(innovationAreas, that.innovationAreas)
                .append(businessType, that.businessType)
                .append(skillsAreas, that.skillsAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(innovationAreas)
                .append(businessType)
                .append(skillsAreas)
                .toHashCode();
    }
}
