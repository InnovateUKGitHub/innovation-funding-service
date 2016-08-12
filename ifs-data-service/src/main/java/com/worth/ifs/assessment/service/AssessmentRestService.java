package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.Assessment} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(final Long id);

    RestResult<Void> recommend(final Long id, final ProcessOutcomeResource processOutcome);

    RestResult<Void> rejectInvitation(final Long id, final ProcessOutcomeResource processOutcome);
}