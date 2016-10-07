package com.worth.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Innovation and Skills view.
 */
public class AssessorProfileSkillsViewModel {
    private String expertise;

    public AssessorProfileSkillsViewModel(String expertise) {
        this.expertise = expertise;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileSkillsViewModel that = (AssessorProfileSkillsViewModel) o;

        return new EqualsBuilder()
                .append(expertise, that.expertise)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(expertise)
                .toHashCode();
    }
}
