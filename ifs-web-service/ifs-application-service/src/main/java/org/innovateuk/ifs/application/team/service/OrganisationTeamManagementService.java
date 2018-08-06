package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serves as a service for invite retrieval / manipulation for an existing {@Organisation} with or without {@InviteOrganisation}.
 */
@Service
public class OrganisationTeamManagementService extends AbstractTeamManagementService {

    private UserRestService userRestService;

    public OrganisationTeamManagementService(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    public ApplicationTeamManagementViewModel createViewModel(long applicationId, long organisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                applicationId, organisationId, loggedInUser.getId());
    }

    public ServiceResult<Void> executeStagedInvite(long applicationId,
                                                                       long organisationId,
                                                                       ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = mapStagedInviteToInviteResource(form, applicationId, organisationId);
        return inviteRestService.createInvitesByOrganisationForApplication(applicationId, organisationId, Arrays.asList(invite)).toServiceResult();
    }

    public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationId) {
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        if (processRoles.stream().anyMatch(processRoleResource -> organisationId.equals(processRoleResource.getOrganisationId()))) {
            return true;
        }
        return false;
    }

    public List<Long> getInviteIds(long applicationId, long organisationId) {
        List<ApplicationInviteResource> invites = inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
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
