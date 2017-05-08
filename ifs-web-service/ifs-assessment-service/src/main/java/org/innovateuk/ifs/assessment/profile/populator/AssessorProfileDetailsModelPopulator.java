package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Details view.
 */
@Component
public class AssessorProfileDetailsModelPopulator {

    @Autowired
    ProfileRestService profileRestService;

    public AssessorProfileDetailsViewModel populateModel(UserResource user) {
        return new AssessorProfileDetailsViewModel(profileRestService.getUserProfile(user.getId()).getSuccessObjectOrThrowException());
    }
}
