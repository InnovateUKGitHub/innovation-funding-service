package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.cofunder.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AssessmentResource} related data.
 */
public interface CofunderAssignmentRestService {

    RestResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    RestResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    RestResult<Void> assign(AssignCofundersResource assignCofundersResource);

    RestResult<Void> removeAssignment(long userId, long applicationId);

    RestResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    RestResult<Void> edit(long assignmentId);

    RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, int page);

    RestResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, int page);

    RestResult<List<Long>> findAllAvailableCofunderUserIdsForApplication(long applicationId, String filter);
}
