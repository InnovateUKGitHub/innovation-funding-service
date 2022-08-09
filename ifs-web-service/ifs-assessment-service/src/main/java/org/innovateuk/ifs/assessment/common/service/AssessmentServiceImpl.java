package org.innovateuk.ifs.assessment.common.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link org.innovateuk.ifs.assessment.resource.AssessmentResource} related data,
 * through the RestService {@link AssessmentRestService}.
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Override
    public AssessmentResource getById(Long id) {
        return assessmentRestService.getById(id).getSuccess();
    }

    @Override
    public AssessmentResource getAssignableById(Long id) {
        return assessmentRestService.getAssignableById(id).getSuccess();
    }

    @Override
    public AssessmentResource getRejectableById(Long id) {
        return assessmentRestService.getRejectableById(id).getSuccess();
    }

    @Override
    public List<AssessmentResource> getByUserAndCompetition(Long userId, Long competitionId) {
        return assessmentRestService.getByUserAndCompetition(userId, competitionId).getSuccess();
    }

    @Override
    public AssessmentTotalScoreResource getTotalScore(Long assessmentId) {
        return assessmentRestService.getTotalScore(assessmentId).getSuccess();
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment) {
        return assessmentRestService.recommend(assessmentId, new AssessmentDecisionOutcomeResourceBuilder()
                .setFundingConfirmation(fundingConfirmation)
                .setFeedback(feedback)
                .setComment(comment)
                .createAssessmentDecisionResource()).toServiceResult();
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, AssessmentRejectOutcomeValue reason, String comment) {
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = new AssessmentRejectOutcomeResource();
        assessmentRejectOutcomeResource.setRejectReason(reason);
        assessmentRejectOutcomeResource.setRejectComment(comment);

        return assessmentRestService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> acceptInvitation(Long assessmentId) {
        return assessmentRestService.acceptInvitation(assessmentId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> submitAssessments(List<Long> assessmentIds) {
        AssessmentSubmissionsResource assessmentSubmissions = new AssessmentSubmissionsResource();
        assessmentSubmissions.setAssessmentIds(assessmentIds);

        return assessmentRestService.submitAssessments(assessmentSubmissions).toServiceResult();
    }
}
