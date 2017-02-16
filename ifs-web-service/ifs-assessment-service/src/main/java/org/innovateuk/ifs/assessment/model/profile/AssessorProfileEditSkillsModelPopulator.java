package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileEditSkillsViewModel;
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