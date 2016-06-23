package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.Assessment} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(final Long id);

}