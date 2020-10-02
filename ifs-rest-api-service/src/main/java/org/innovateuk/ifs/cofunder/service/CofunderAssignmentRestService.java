package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Interface for CRUD operations on {@link AssessmentResource} related data.
 */
public interface CofunderAssignmentRestService {

    RestResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    RestResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    RestResult<Void> removeAssignment(long userId, long applicationId);

    RestResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    RestResult<Void> edit(long assignmentId);

    RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, int page);

    RestResult<CofundersAvailableForApplicationPageResource> findAvailableCofudersForApplication(long applicationId, String filter, int page);

}
