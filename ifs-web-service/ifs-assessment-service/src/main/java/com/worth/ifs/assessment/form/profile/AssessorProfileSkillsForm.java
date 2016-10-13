package com.worth.ifs.assessment.form.profile;

import com.worth.ifs.commons.validation.constraints.WordCount;
import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.user.resource.BusinessType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form field model for the Assessor Profile Skill Areas page
 */
public class AssessorProfileSkillsForm extends BaseBindingResultTarget {

    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String skillAreas;

    @NotNull(message = "{validation.standard.assessortype.required}")
    private BusinessType assessorType;

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

        AssessorProfileSkillsForm that = (AssessorProfileSkillsForm) o;

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
