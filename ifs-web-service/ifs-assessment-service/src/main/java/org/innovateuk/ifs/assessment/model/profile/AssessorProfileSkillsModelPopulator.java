package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populater for assessor skills view model.
 */
@Component
public class AssessorProfileSkillsModelPopulator {

    @Autowired
    private UserService userService;

    public AssessorProfileSkillsViewModel populateModel(Long userId) {
        ProfileSkillsResource profileSkillsResource = userService.getProfileSkills(userId);
        return new AssessorProfileSkillsViewModel(profileSkillsResource.getSkillsAreas(), profileSkillsResource.getBusinessType());
    }
}
