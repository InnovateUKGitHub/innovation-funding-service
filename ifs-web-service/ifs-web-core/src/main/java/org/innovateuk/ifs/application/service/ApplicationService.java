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
    @NotSecured("Not currently secured")
    ApplicationResource getById(Long applicationId);
    @NotSecured("Not currently secured")
    Boolean isApplicationReadyForSubmit(Long applicationId);
    @NotSecured("Not currently secured")
    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    @NotSecured("Not currently secured")
    ServiceResult<Void> save(ApplicationResource application);
    @NotSecured("Not currently secured")
    OrganisationResource getLeadOrganisation(Long applicationId);
    @NotSecured("Not currently secured")
    ServiceResult<Void> removeCollaborator(Long applicationInviteId);
    @NotSecured("Not currently secured")
    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);
    @NotSecured("Not currently secured")
    Boolean showApplicationTeam(Long applicationId, Long userid);
}
