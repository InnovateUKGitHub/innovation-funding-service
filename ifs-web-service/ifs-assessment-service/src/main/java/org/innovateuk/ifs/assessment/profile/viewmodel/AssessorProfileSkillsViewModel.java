package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.Map;

/**
 * Holder of model attributes for the Assessor skills view.
 */
public class AssessorProfileSkillsViewModel extends AssessorProfileBaseSkillsViewModel {
    private String skillAreas;
    private BusinessType assessorType;

    public AssessorProfileSkillsViewModel(Map<String, List<String>> innovationAreas, String skillAreas, BusinessType assessorType) {
        super(innovationAreas);
        this.skillAreas = skillAreas;
        this.assessorType = assessorType;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public BusinessType getAssessorType() {
        return assessorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileSkillsViewModel that = (AssessorProfileSkillsViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(skillAreas, that.skillAreas)
                .append(assessorType, that.assessorType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(skillAreas)
                .append(assessorType)
                .toHashCode();
    }
}
