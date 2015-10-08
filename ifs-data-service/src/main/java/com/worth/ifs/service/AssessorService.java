package com.worth.ifs.service;

import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceSuccess;
import com.worth.ifs.util.Either;

import java.util.Optional;

/**
 * Created by dwatson on 06/10/15.
 */
public interface AssessorService {

    Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Long responseId, Long assessorProcessRoleId, Optional<String> feedbackValue, Optional<String> feedbackText);

}
