package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.profile.service.ProfileService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Details view.
 */
@Component
public class AssessorProfileDetailsModelPopulator {

    @Autowired
    ProfileService profileService;

    public AssessorProfileDetailsViewModel populateModel(UserResource user) {
        return new AssessorProfileDetailsViewModel(profileService.getUserProfile(user.getId()));
    }
}
