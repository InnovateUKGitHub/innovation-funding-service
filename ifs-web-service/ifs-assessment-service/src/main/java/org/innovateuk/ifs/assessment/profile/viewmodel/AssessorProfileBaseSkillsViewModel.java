package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

/**
 * Abstract holder of model attributes for the Assessor skills views.
 */
public abstract class AssessorProfileBaseSkillsViewModel {

    private Map<String, List<String>> innovationAreas;

    protected AssessorProfileBaseSkillsViewModel(Map<String, List<String>> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public Map<String, List<String>> getInnovationAreas() {
        return innovationAreas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileBaseSkillsViewModel that = (AssessorProfileBaseSkillsViewModel) o;

        return new EqualsBuilder()
                .append(innovationAreas, that.innovationAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(innovationAreas)
                .toHashCode();
    }
}