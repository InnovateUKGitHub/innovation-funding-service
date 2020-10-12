package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;

public interface CofunderAssignmentService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> edit(long assignmentId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, Pageable pageable);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, Pageable pageable);

}
