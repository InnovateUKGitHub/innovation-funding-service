package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.form.registration.AssessorRegistrationForm;
import com.worth.ifs.commons.service.ServiceResult;

public interface AssessorService {

    ServiceResult<Void> createAssessorByInviteHash(String inviteHash, AssessorRegistrationForm registrationForm);
}
