package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;

public interface CofunderAssignmentService {

    ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    ServiceResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    ServiceResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    ServiceResult<Void> edit(long assignmentId);

    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, Pageable pageable);

    ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofudersForApplication(long applicationId, Pageable pageable);

}
