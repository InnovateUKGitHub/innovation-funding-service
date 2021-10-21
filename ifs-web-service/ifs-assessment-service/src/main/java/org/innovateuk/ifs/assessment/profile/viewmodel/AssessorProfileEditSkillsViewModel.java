package org.innovateuk.ifs.assessment.profile.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.viewmodel.AssessorProfileBaseSkillsViewModel;

import java.util.List;
import java.util.Map;

/**
 * Holder of model attributes for the Assessor edit skills view.
 */
public class AssessorProfileEditSkillsViewModel extends AssessorProfileBaseSkillsViewModel {

    public AssessorProfileEditSkillsViewModel(Map<String, List<String>> innovationAreas) {
        super(innovationAreas);
    }

    @JsonIgnore
    public BusinessType[] getAllAssessorTypes() {
        return BusinessType.values();
    }
}