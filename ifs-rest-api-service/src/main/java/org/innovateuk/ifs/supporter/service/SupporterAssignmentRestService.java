package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.supporter.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AssessmentResource} related data.
 */
public interface SupporterAssignmentRestService {

    RestResult<SupporterAssignmentResource> getAssignment(long userId, long applicationId);

    RestResult<List<SupporterAssignmentResource>> getAssignmentsByApplicationId(long applicationId);

    RestResult<SupporterAssignmentResource> assign(long userId, long applicationId);

    RestResult<Void> assign(AssignSupportersResource assignSupportersResource);

    RestResult<Void> removeAssignment(long userId, long applicationId);

    RestResult<Void> decision(long assignmentId, SupporterDecisionResource decision);

    RestResult<Void> edit(long assignmentId);

    RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingSupporters(long competitionId, String filter, int page);

    RestResult<SupportersAvailableForApplicationPageResource> findAvailableSupportersForApplication(long applicationId, String filter, int page);

    RestResult<List<Long>> findAllAvailableSupporterUserIdsForApplication(long applicationId, String filter);
}
