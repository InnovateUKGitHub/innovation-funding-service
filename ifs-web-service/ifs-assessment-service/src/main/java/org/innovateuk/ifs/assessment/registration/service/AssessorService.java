package org.innovateuk.ifs.assessment.registration.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.form.RegistrationForm;

public interface AssessorService {

    ServiceResult<Void> createAssessorByInviteHash(String inviteHash, RegistrationForm registrationForm, AddressResource address);
}
