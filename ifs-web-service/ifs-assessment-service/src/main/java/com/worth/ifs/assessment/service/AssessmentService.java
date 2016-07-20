package com.worth.ifs.assessment.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentService {

    AssessmentResource getById(Long id);

    List<QuestionResource> getAllQuestionsById(Long assessmentId) throws ExecutionException, InterruptedException;

    ServiceResult<Void> rejectApplication(Long assessmentId, String reason, String comment);

}
