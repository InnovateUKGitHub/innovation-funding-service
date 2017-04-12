package org.innovateuk.ifs.assessment.registration.service;

import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains a method to create an Assessor from a {@link org.innovateuk.ifs.user.resource.UserResource}
 * through the RestService {@link AssessorRestService}.
 */
@Service
public class AssessorServiceImpl implements AssessorService {

    @Autowired
    private AssessorRestService assessorRestService;

    @Override
    public ServiceResult<Void> createAssessorByInviteHash(String inviteHash, AssessorRegistrationForm registrationForm) {
        UserRegistrationResource userRegistrationResource = new UserRegistrationResource();
        userRegistrationResource.setFirstName(registrationForm.getFirstName());
        userRegistrationResource.setLastName(registrationForm.getLastName());
        userRegistrationResource.setPhoneNumber(registrationForm.getPhoneNumber());
        userRegistrationResource.setGender(registrationForm.getGender());
        userRegistrationResource.setDisability(registrationForm.getDisability());
        userRegistrationResource.setEthnicity(registrationForm.getEthnicity());
        userRegistrationResource.setPassword(registrationForm.getPassword());
        userRegistrationResource.setAddress(registrationForm.getAddressForm().getSelectedPostcode());

        return assessorRestService.createAssessorByInviteHash(inviteHash, userRegistrationResource).toServiceResult();
    }
}
