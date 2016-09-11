package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.Assessment} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(final Long id);

    RestResult<List<AssessmentResource>> getByUserId(final Long userId);

    RestResult<Void> recommend(final Long id, final ProcessOutcomeResource processOutcome);

    RestResult<Void> rejectInvitation(final Long id, final ProcessOutcomeResource processOutcome);
}