package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Component
public class AssessorProfileDetailsModelPopulator {

    public AssessorProfileDetailsViewModel populateModel(UserResource user, ProfileResource profile) {
        return new AssessorProfileDetailsViewModel(
                user,
                profile
        );
    }
}
