package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileEditDetailsViewModel;
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
