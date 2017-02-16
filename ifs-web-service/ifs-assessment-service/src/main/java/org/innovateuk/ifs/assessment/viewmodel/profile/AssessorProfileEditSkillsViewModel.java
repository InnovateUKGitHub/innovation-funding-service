package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Holder of model attributes for the Assessor edit skills view.
 */
public class AssessorProfileEditSkillsViewModel {

    private List<InnovationAreaResource> innovationAreas;

    public AssessorProfileEditSkillsViewModel(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
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

        AssessorProfileEditSkillsViewModel that = (AssessorProfileEditSkillsViewModel) o;

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