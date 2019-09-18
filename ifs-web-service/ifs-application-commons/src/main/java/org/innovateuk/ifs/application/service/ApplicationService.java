package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationService {
    ApplicationResource getById(Long applicationId);
    ApplicationResource createApplication(long competitionId, long userId, long organisationId, String applicationName);
    ServiceResult<ValidationMessages> save(ApplicationResource application);
    OrganisationResource getLeadOrganisation(Long applicationId);
    ServiceResult<Void> removeCollaborator(Long applicationInviteId);
    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);
}
