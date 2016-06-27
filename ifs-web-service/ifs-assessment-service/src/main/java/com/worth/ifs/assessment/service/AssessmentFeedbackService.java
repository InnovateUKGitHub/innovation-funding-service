package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentFeedbackResource} related data.
 */
public interface AssessmentFeedbackService {

    List<AssessmentFeedbackResource> getAllAssessmentFeedback(Long assessmentId);

    AssessmentFeedbackResource getAssessmentFeedbackByAssessmentAndQuestion(Long assessmentId, Long questionId);

    ServiceResult<Void> updateFeedbackValue(Long assessmentId, Long questionId, String value);

    ServiceResult<Void> updateFeedbackScore(Long assessmentId, Long questionId, Integer score);

}
