package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ApplicationResource} related data,
 * through the RestService {@link ApplicationRestService}.
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public ApplicationResource getById(Long applicationId) {
        if (applicationId == null) {
            return null;
        }
        return applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ApplicationResource createApplication(Long competitionId, Long userId, String applicationName) {
        return applicationRestService.createApplication(competitionId, userId, applicationName).getSuccessObjectOrThrowException();
    }

    @Override
    public Boolean isApplicationReadyForSubmit(Long applicationId) {
        return applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateState(Long applicationId, ApplicationState state) {
        return applicationRestService.updateApplicationState(applicationId, state).toServiceResult();
    }

    @Override
    public ServiceResult<Void> save(ApplicationResource application) {
        return applicationRestService.saveApplication(application).toServiceResult();
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long applicationId) {
        ApplicationResource application = getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
    }

    @Override
    public ServiceResult<Void> removeCollaborator(Long applicationInviteId) {
        return inviteRestService.removeApplicationInvite(applicationInviteId).toServiceResult();
    }
}
