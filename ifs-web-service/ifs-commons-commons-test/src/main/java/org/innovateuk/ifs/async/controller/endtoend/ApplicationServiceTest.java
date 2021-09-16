package org.innovateuk.ifs.async.controller.endtoend;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServiceTest implements ApplicationService {

    @Override
    public ApplicationResource getById(Long applicationId) {
        return new ApplicationResource();
    }

    @Override
    public ApplicationResource createApplication(long competitionId, long userId, long organisationId, String applicationName) {
        return new ApplicationResource();
    }

    @Override
    public ServiceResult<ValidationMessages> save(ApplicationResource application) {
        return ServiceResult.serviceSuccess(null);
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long applicationId) {
        return new OrganisationResource();
    }

    @Override
    public ServiceResult<Void> removeCollaborator(Long applicationInviteId) {
        return ServiceResult.serviceSuccess();
    }

    @Override
    public ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason) {
        return ServiceResult.serviceSuccess();
    }
}
