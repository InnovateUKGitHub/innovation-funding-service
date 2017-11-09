package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationService {
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationResource getById(Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean isApplicationReadyForSubmit(Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> save(ApplicationResource application);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getLeadOrganisation(Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeCollaborator(Long applicationInviteId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean showApplicationTeam(Long applicationId, Long userid);
}
