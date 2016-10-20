package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDetailsViewModel;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Details view.
 */
@Component
public class AssessorProfileDetailsModelPopulator {

    @Autowired
    EthnicityRestService ethnicityRestService;

    @Autowired
    UserService userService;

    public AssessorProfileDetailsViewModel populateModel(UserResource user, EthnicityResource ethnicity) {

        AddressResource address = new AddressResource();
        ProfileAddressResource profileAddress = userService.getProfileAddress(user.getId());

        if (profileAddress.getAddress() != null) {
            address = profileAddress.getAddress();
        }

        return new AssessorProfileDetailsViewModel(user, address, ethnicity);
    }
}
