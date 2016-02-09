package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 */
public interface AssessorService {

    /**
     * Update the Assessor's feedback to a given Response, creating a new AssessorFeedback if one does not yet
     * exist for this Assessor
     */
    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.dto.Feedback', 'UPDATE')")
    ServiceResult<Feedback> updateAssessorFeedback(@P("id") Feedback.Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.dto.Feedback', 'READ')")
    ServiceResult<Feedback> getFeedback(@P("id") Feedback.Id id);


}
