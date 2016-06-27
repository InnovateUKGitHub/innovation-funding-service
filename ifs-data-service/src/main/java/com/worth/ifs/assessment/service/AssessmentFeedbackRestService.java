package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.AssessmentFeedback} related data.
 */
public interface AssessmentFeedbackRestService {

    RestResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(Long assessmentId);

    RestResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(Long assessmentId, Long questionId);

    RestResult<Void> updateFeedbackValue(Long assessmentId, Long questionId, String value);

    RestResult<Void> updateFeedbackScore(Long assessmentId, Long questionId, Integer score);

}