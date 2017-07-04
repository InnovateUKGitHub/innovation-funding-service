package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Supplier;

/**
 * Serves as a base service for invite retrieval / editing in instances of {@AbstractApplicationTeamManagementController}.
 */
public abstract class AbstractTeamManagementService {

    @Autowired
    protected InviteRestService inviteRestService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @Autowired
    protected InviteOrganisationRestService inviteOrganisationRestService;

    public abstract String validateOrganisationAndApplicationIds(Long applicationId,
                                                                    Long organisationId,
                                                                    Supplier<String> supplier);

    public abstract ApplicationTeamManagementViewModel createViewModel(long applicationId,
                                                                          long organisationId,
                                                                          UserResource loggedInUser);

    public abstract ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                                long organisationId,
                                                                                ApplicationTeamUpdateForm form);

    public abstract List<Long> getInviteIds(long applicationId, long organisationId);

    public ApplicationInviteResource createInvite(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                     long applicationId,
                                                     Long inviteOrganisationId) {

        return createInvite(applicationTeamUpdateForm.getStagedInvite(), applicationId, inviteOrganisationId);
    }

    private ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm,
                                                     long applicationId,
                                                     Long inviteOrganisationId) {
        ApplicationInviteResource applicationInviteResource = new ApplicationInviteResource(
                applicantInviteForm.getName(),
                applicantInviteForm.getEmail(),
                applicationId
        );
        applicationInviteResource.setInviteOrganisation(inviteOrganisationId);

        return applicationInviteResource;
    }

    public ServiceResult<Void> removeInvite(long applicantInviteId) {
        return applicationService.removeCollaborator(applicantInviteId);
    }
}
