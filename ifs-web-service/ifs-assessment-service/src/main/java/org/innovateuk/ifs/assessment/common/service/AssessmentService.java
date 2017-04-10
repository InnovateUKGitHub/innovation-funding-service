package org.innovateuk.ifs.assessment.common.service;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentService {

    AssessmentResource getById(Long id);

    AssessmentResource getAssignableById(Long id);

    AssessmentResource getRejectableById(Long id);

    List<AssessmentResource> getByUserAndCompetition(Long userId, Long competitionId);

    AssessmentTotalScoreResource getTotalScore(Long assessmentId);

    ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment);

    ServiceResult<Void> rejectInvitation(Long assessmentId, AssessmentRejectOutcomeValue reason, String comment);

    void acceptInvitation(Long assessmentId);

    ServiceResult<Void> submitAssessments(List<Long> assessmentIds);
}
