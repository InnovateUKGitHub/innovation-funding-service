package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @NotSecured("Not currently secured")
    public abstract boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationId);

    @NotSecured("Not currently secured")
    public abstract ApplicationTeamManagementViewModel createViewModel(long applicationId,
                                                                          long organisationId,
                                                                          UserResource loggedInUser);
    @NotSecured("Not currently secured")
    public abstract ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                                long organisationId,
                                                                                ApplicationTeamUpdateForm form);
    @NotSecured("Not currently secured")
    public abstract List<Long> getInviteIds(long applicationId, long organisationId);

    @NotSecured("Not currently secured")
    protected ApplicationInviteResource mapStagedInviteToInviteResource(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                                        long applicationId,
                                                                        Long inviteOrganisationId) {

        ApplicationInviteResource applicationInviteResource = new ApplicationInviteResource(
                applicationTeamUpdateForm.getStagedInvite().getName(),
                applicationTeamUpdateForm.getStagedInvite().getEmail(),
                applicationId
        );
        applicationInviteResource.setInviteOrganisation(inviteOrganisationId);

        return applicationInviteResource;
    }

    @NotSecured("Not currently secured")
    public ServiceResult<Void> removeInvite(long applicantInviteId) {
        return applicationService.removeCollaborator(applicantInviteId);
    }
}
