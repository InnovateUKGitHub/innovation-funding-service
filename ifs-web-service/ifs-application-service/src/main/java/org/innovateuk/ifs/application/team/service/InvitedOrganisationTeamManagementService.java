package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * Serves as a service for invite retrieval / manipulation for an {@InviteOrganisation} without an existing {@Organisation}.
 */
@Service
public class InvitedOrganisationTeamManagementService extends AbstractTeamManagementService {

    public ApplicationTeamManagementViewModel createViewModel(long applicationId, long inviteOrganisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(
                applicationId, inviteOrganisationId, loggedInUser.getId());
    }

    public ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                    long inviteOrganisationId,
                                                                    ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = mapStagedInviteToInviteResource(form, applicationId, inviteOrganisationId);

        return inviteRestService.saveInvites(singletonList(invite)).toServiceResult();
    }

    @Override
    public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationInviteId) {

        InviteOrganisationResource organisation = inviteOrganisationRestService.getById(organisationInviteId).getSuccess();
        return simpleAnyMatch(organisation.getInviteResources(), invite -> invite.getApplication().equals(applicationId));
    }

    public List<Long> getInviteIds(long applicationId, long organisationId) {
        List<ApplicationInviteResource> invites = inviteOrganisationRestService.getById(organisationId)
                .getSuccess()
                .getInviteResources();

        if(invites != null) {
            return invites.stream()
                    .map(ApplicationInviteResource::getId)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
