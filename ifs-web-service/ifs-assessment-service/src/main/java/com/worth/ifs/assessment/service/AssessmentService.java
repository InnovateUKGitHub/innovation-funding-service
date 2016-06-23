package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentService {

    AssessmentResource getById(Long id);

}
