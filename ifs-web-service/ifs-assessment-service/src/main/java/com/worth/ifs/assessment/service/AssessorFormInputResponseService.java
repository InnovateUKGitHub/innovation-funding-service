package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessorFormInputResponseResource} related data.
 */
public interface AssessorFormInputResponseService {

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponses(Long assessmentId);

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

}