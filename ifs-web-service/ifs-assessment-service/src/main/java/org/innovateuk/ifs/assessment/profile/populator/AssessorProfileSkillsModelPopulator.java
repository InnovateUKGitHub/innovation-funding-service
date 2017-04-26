package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.springframework.stereotype.Component;

/**
 * Populator for the Assessor Skills view.
 */
@Component
public class AssessorProfileSkillsModelPopulator extends AssessorProfileBaseSkillsModelPopulator<AssessorProfileSkillsViewModel> {

    @Override
    public AssessorProfileSkillsViewModel populateModel(ProfileSkillsResource profileSkillsResource) {
        return new AssessorProfileSkillsViewModel(getInnovationAreasSectorMap(profileSkillsResource),
                profileSkillsResource.getSkillsAreas(), profileSkillsResource.getBusinessType());
    }
}
