package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Populator for assessor skills view model.
 */
@Component
public class AssessorProfileSkillsModelPopulator {

    @Autowired
    private UserService userService;

    public AssessorProfileSkillsViewModel populateModel(Long userId) {
        ProfileSkillsResource profileSkillsResource = userService.getProfileSkills(userId);

        List<String> innovationAreas = simpleMap(profileSkillsResource.getInnovationAreas(),
                InnovationAreaResource::getName);

        return new AssessorProfileSkillsViewModel(innovationAreas, profileSkillsResource.getSkillsAreas(),
                profileSkillsResource.getBusinessType());
    }
}
