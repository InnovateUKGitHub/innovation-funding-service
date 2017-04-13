package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileEditSkillsViewModel;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.springframework.stereotype.Component;

/**
 * Populator for the Assessor Skills Edit view.
 */
@Component
public class AssessorProfileEditSkillsModelPopulator extends AssessorProfileBaseSkillsModelPopulator<AssessorProfileEditSkillsViewModel> {

    public AssessorProfileEditSkillsViewModel populateModel(ProfileSkillsResource profileSkillsResource) {
        return new AssessorProfileEditSkillsViewModel(getInnovationAreasSectorMap(profileSkillsResource));
    }

}