package org.innovateuk.ifs.assessment.common.service;

import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource} related data.
 */
public interface AssessorFormInputResponseService {

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponses(Long assessmentId);

    List<AssessorFormInputResponseResource> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    ServiceResult<Void> updateFormInputResponse(Long assessmentId,Long formInputId,String value);

}
