package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Component
public class AssessorProfileDetailsModelPopulator {

    private ProfileRestService profileRestService;

    public AssessorProfileDetailsModelPopulator(ProfileRestService profileRestService) {
        this.profileRestService = profileRestService;
    }

    public AssessorProfileDetailsViewModel populateModel(UserResource user) {
        return new AssessorProfileDetailsViewModel(
                profileRestService.getUserProfile(user.getId()).getSuccess()
        );
    }
}
