package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDetailsViewModel;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Details view.
 */
@Component
public class AssessorProfileDetailsModelPopulator {

    @Autowired
    EthnicityRestService ethnicityRestService;

    public AssessorProfileDetailsViewModel populateModel(UserResource user, EthnicityResource ethnicity) {

        AddressResource address = new AddressResource();
  /*      ProfileResource profile = user.getProfile();
        if (profile != null) {
            address = profile.getAddress();
        }*/


        return new AssessorProfileDetailsViewModel(user, address, ethnicity);
    }
}
