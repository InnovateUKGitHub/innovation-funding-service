package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessorFormInputResponseResource} related data.
 */
public interface AssessorFormInputResponseService {

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponses(Long assessmentId);

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    ServiceResult<Void> updateFormInputResponse(Long assessmentId,Long formInputId,String value);

}