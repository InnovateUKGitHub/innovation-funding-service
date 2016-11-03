package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.assessment.domain.Assessment} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(Long id);

    RestResult<List<AssessmentResource>> getByUserAndCompetition(Long userId, Long CompetitionId);

    RestResult<Void> recommend(Long id, AssessmentFundingDecisionResource assessmentFundingDecision);

    RestResult<Void> rejectInvitation(Long id, ApplicationRejectionResource applicationRejection);

    RestResult<Void> acceptInvitation(Long id);

}