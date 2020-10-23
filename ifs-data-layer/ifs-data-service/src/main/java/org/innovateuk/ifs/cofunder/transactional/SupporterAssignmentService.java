package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupporterAssignmentService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<SupporterAssignmentResource> getAssignment(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<SupporterAssignmentResource>> getAssignmentsByApplicationId(long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<SupporterAssignmentResource> assign(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> assign(List<Long> userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> decision(long assignmentId, SupporterDecisionResource decision);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> edit(long assignmentId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingSupporters(long competitionId, String filter, Pageable pageable);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<SupportersAvailableForApplicationPageResource> findAvailableSupportersForApplication(long applicationId, String filter, Pageable pageable);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<Long>> findAvailableSupportersUserIdsForApplication(long applicationId, String filter);

}
