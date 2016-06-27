package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessmentFeedback} data.
 */
public interface AssessmentFeedbackService {

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(Long assessmentId);

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(Long assessmentId, Long questionId);

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateFeedbackValue(Long assessmentId, Long questionId, String value);

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateFeedbackScore(Long assessmentId, Long questionId, Integer score);

}