package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentService {

    AssessmentResource getById(Long id);

    List<AssessmentResource> getByUserAndCompetition(Long userId, Long competitionId);

    ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment);

    ServiceResult<Void> rejectInvitation(Long assessmentId, String reason, String comment);

    ServiceResult<Void> acceptInvitation(Long assessmentId);

    ServiceResult<Void> submitAssessments(List<Long> assessmentIds);
}
