package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(Long id);

    RestResult<List<AssessmentResource>> getByUserAndCompetition(Long userId, Long CompetitionId);

    RestResult<Void> recommend(Long id, ProcessOutcomeResource processOutcome);

    RestResult<Void> rejectInvitation(Long id, ProcessOutcomeResource processOutcome);
}