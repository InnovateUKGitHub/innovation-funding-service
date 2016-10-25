package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDetailsViewModel;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileEditDetailsViewModel;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.UserResource;
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
