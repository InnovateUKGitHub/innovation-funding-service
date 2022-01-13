package org.innovateuk.ifs.viewmodel;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * Abstract holder of model attributes for the Assessor skills views.
 */
@EqualsAndHashCode
public abstract class AssessorProfileBaseSkillsViewModel {

    private Map<String, List<String>> innovationAreas;

    protected AssessorProfileBaseSkillsViewModel(Map<String, List<String>> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public Map<String, List<String>> getInnovationAreas() {
        return innovationAreas;
    }

}