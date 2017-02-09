package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Size;

/**
 * Base class for profile skills DTOs
 */
public abstract class ProfileSkillsBaseResource {

    private Long user;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    private String skillsAreas;
    private BusinessType businessType;

    protected ProfileSkillsBaseResource() {
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getSkillsAreas() {
        return skillsAreas;
    }

    public void setSkillsAreas(String skillsAreas) {
        this.skillsAreas = skillsAreas;
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

        ProfileSkillsBaseResource that = (ProfileSkillsBaseResource) o;

        return new EqualsBuilder()
                .append(user, that.user)
                .append(skillsAreas, that.skillsAreas)
                .append(businessType, that.businessType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(skillsAreas)
                .append(businessType)
                .toHashCode();
    }
}