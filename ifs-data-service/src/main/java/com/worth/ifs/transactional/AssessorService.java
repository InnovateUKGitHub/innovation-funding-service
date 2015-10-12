package com.worth.ifs.transactional;

import com.worth.ifs.util.Either;

import java.util.Optional;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 *
 * Created by dwatson on 06/10/15.
 */
public interface AssessorService {

    /**
     * Update the Assessor's feedback to a given Response, creating a new AssessorFeedback if one does not yet
     * exist for this Assessor
     *
     * @param responseId
     * @param assessorProcessRoleId
     * @param feedbackValue
     * @param feedbackText
     * @return
     */
    Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Long responseId, Long assessorProcessRoleId, Optional<String> feedbackValue, Optional<String> feedbackText);

}
