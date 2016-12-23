package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

/**
 * Holder of model attributes for the Assessor user skills view.
 */
public class AssessorProfileSkillsViewModel {
    private String skillAreas;
    private BusinessType assessorType;

    public AssessorProfileSkillsViewModel(String skillAreas, BusinessType assessorType) {
        this.skillAreas = skillAreas;
        this.assessorType = assessorType;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
    }

    public BusinessType getAssessorType() {
        return assessorType;
    }

    public void setAssessorType(BusinessType assessorType) {
        this.assessorType = assessorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileSkillsViewModel that = (AssessorProfileSkillsViewModel) o;

        return new EqualsBuilder()
                .append(skillAreas, that.skillAreas)
                .append(assessorType, that.assessorType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skillAreas)
                .append(assessorType)
                .toHashCode();
    }
}
