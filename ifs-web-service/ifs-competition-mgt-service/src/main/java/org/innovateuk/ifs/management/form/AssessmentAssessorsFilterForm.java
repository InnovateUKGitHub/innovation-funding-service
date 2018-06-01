package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.BusinessType;

/**
 * Form for filtering assessors by {@link BusinessType} and {@link org.innovateuk.ifs.category.resource.InnovationSectorResource}.
 */
public class AssessmentAssessorsFilterForm extends BaseBindingResultTarget {

    private Long innovationSector = null;
    private BusinessType businessType = null;

    public Long getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(Long innovationSector) {
        this.innovationSector = innovationSector;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentAssessorsFilterForm that = (AssessmentAssessorsFilterForm) o;

        return new EqualsBuilder()
                .append(innovationSector, that.innovationSector)
                .append(businessType, that.businessType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(innovationSector)
                .append(businessType)
                .toHashCode();
    }
}