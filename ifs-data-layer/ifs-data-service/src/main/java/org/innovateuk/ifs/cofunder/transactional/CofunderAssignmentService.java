package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface CofunderAssignmentService {

    ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    ServiceResult<Void> assign(long userId, long applicationId);

    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    ServiceResult<Void> accept(long assignmentId);

    ServiceResult<Void> reject(long assignmentId);

    ServiceResult<Void> edit(long assignmentId);

    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId);

    ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofudersForApplication(long applicationId);

}
