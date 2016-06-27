package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessmentFeedbackResource} related data,
 * through the RestService {@link AssessmentFeedbackRestService}.
 */
@Service
public class AssessmentFeedbackServiceImpl implements AssessmentFeedbackService {

    @Autowired
    private AssessmentFeedbackRestService assessmentFeedbackRestService;

    @Override
    public List<AssessmentFeedbackResource> getAllAssessmentFeedback(final Long assessmentId) {
        return assessmentFeedbackRestService.getAllAssessmentFeedback(assessmentId).getSuccessObjectOrThrowException();
    }

    @Override
    public AssessmentFeedbackResource getAssessmentFeedbackByAssessmentAndQuestion(final Long assessmentId, final Long questionId) {
        return assessmentFeedbackRestService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateFeedbackValue(final Long assessmentId, final Long questionId, final String value) {
        return assessmentFeedbackRestService.updateFeedbackValue(assessmentId, questionId, value).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateFeedbackScore(final Long assessmentId, final Long questionId, final Integer score) {
        return assessmentFeedbackRestService.updateFeedbackScore(assessmentId, questionId, score).toServiceResult();
    }
}