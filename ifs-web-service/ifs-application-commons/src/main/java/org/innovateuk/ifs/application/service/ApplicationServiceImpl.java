package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private OrganisationRestService organisationRestService;

    @Override
    public ApplicationResource getById(Long applicationId) {
        if (applicationId == null) {
            return null;
        }
        return applicationRestService.getApplicationById(applicationId).getSuccess();
    }

    @Override
    public ApplicationResource createApplication(long competitionId, long userId, long organisationId, String applicationName) {
        return applicationRestService.createApplication(competitionId, userId, organisationId, applicationName).getSuccess();
    }

    @Override
    public ServiceResult<ValidationMessages> save(ApplicationResource application) {
        return applicationRestService.saveApplication(application).toServiceResult();
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long applicationId) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRole(applicationId);
        return organisationRestService.getOrganisationById(leadApplicantProcessRole.getOrganisationId()).getSuccess();
    }

    @Override
    public ServiceResult<Void> removeCollaborator(Long applicationInviteId) {
        return inviteRestService.removeApplicationInvite(applicationInviteId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason) {
        return applicationRestService.markAsIneligible(applicationId, reason).toServiceResult();
    }
}
