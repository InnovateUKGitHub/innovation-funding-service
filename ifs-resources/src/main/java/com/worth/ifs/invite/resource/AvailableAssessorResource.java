package com.worth.ifs.invite.resource;

import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.user.resource.BusinessType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for available assessors.
 */
public class AvailableAssessorResource {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private BusinessType businessType;
    private CategoryResource innovationArea;
    private boolean compliant;
    private boolean added;

    public AvailableAssessorResource() {
    }

    /*
     * TODO Should be able to remove this constructor when INFUND-6775 is completed
     */
    public AvailableAssessorResource(Long userId, String firstName, String lastName, String email, BusinessType businessType, CategoryResource innovationArea, boolean compliant, boolean added) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.businessType = businessType;
        this.innovationArea = innovationArea;
        this.compliant = compliant;
        this.added = added;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
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
                .append(compliant, that.compliant)
                .append(added, that.added)
                .append(userId, that.userId)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(email, that.email)
                .append(businessType, that.businessType)
                .append(innovationArea, that.innovationArea)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(firstName)
                .append(lastName)
                .append(email)
                .append(businessType)
                .append(innovationArea)
                .append(compliant)
                .append(added)
                .toHashCode();
    }
}