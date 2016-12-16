package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileEditDetailsViewModel;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Details edit view.
 */
@Component
public class AssessorProfileEditDetailsModelPopulator {

    public AssessorProfileEditDetailsViewModel populateModel(UserResource loggedInUser) {
        return new AssessorProfileEditDetailsViewModel(loggedInUser.getEmail());
    }
}
