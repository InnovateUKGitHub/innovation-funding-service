package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.form.registration.AssessorRegistrationForm;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface AssessorService {

    ServiceResult<Void> createAssessorByInviteHash(String inviteHash, AssessorRegistrationForm registrationForm);
}
