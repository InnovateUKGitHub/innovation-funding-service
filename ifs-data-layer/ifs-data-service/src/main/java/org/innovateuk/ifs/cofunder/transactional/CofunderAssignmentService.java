package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CofunderAssignmentService {

    ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    ServiceResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    ServiceResult<Void> assign(List<Long> userIds, long applicationId);

    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    ServiceResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    ServiceResult<Void> edit(long assignmentId);

    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, Pageable pageable);

    ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, Pageable pageable);

    ServiceResult<List<Long>> findAvailableCofundersUserIdsForApplication(long applicationId, String filter);

}
