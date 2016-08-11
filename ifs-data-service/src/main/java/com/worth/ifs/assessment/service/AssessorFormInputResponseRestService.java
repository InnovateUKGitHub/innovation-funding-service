package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
public interface AssessorFormInputResponseRestService {

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId);

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    RestResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response);
}
