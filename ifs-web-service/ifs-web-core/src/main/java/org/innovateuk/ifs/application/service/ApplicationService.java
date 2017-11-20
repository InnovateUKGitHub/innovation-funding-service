package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationService {
    ApplicationResource getById(Long applicationId);

    Boolean isApplicationReadyForSubmit(Long applicationId);

    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);

    ServiceResult<Void> save(ApplicationResource application);

    OrganisationResource getLeadOrganisation(Long applicationId);

    ServiceResult<Void> removeCollaborator(Long applicationInviteId);

    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);

    Boolean showApplicationTeam(Long applicationId, Long userid);
}
