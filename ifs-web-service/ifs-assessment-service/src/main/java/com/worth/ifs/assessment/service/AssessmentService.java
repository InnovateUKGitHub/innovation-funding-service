package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentService {

    AssessmentResource getById(Long id);

    ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment);

    ServiceResult<Void> rejectInvitation(Long assessmentId, String reason, String comment);

}
