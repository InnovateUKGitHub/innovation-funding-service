package org.innovateuk.ifs.assessment.registration.service;

import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface AssessorService {

    ServiceResult<Void> createAssessorByInviteHash(String inviteHash, AssessorRegistrationForm registrationForm);
}
