package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serves as a service for invite retrieval / manipulation for an {@InviteOrganisation} without an existing {@Organisation}.
 */
@Service
public class InviteOrganisationTeamManagementService extends AbstractTeamManagementService {
    public ApplicationTeamManagementViewModel createViewModel(long applicationId, long inviteOrganisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(
                applicationId, inviteOrganisationId, loggedInUser.getId());
    }

    public ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                    long inviteOrganisationId,
                                                                    ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = mapStagedInviteToInviteResource(form, applicationId, inviteOrganisationId);

        return inviteRestService.saveInvites(Arrays.asList(invite)).toServiceResult();
    }

    public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationInviteId) {
        InviteOrganisationResource organisation = inviteOrganisationRestService.getById(organisationInviteId).getSuccessObjectOrThrowException();
        if(organisation.getInviteResources().stream().anyMatch(applicationInviteResource -> applicationInviteResource.getApplication().equals(applicationId))) {
            return true;
        }
        return false;
    }

    public List<Long> getInviteIds(long applicationId, long organisationId) {
        return inviteOrganisationRestService.getById(organisationId)
                .getSuccessObjectOrThrowException()
                .getInviteResources().stream()
                .map(ApplicationInviteResource::getId)
                .collect(Collectors.toList());
    }
}
