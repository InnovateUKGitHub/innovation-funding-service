package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserProfileBaseResource;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to encapsulate an Assessors profile view.
 */
public class AssessorProfileResource extends UserProfileBaseResource {

    private List<CategoryResource> innovationAreas = new ArrayList<>();
    private BusinessType businessType;
    private String skillsAreas;
    private List<AffiliationResource> affiliations = new ArrayList<>();

    public List<CategoryResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<CategoryResource> innovationAreas) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileResource that = (AssessorProfileResource) o;

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
