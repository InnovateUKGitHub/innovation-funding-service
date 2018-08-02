package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * Serves as a service for invite retrieval / manipulation for an existing {@Organisation} with or without {@InviteOrganisation}.
 */
@Service
public class ExistingOrganisationTeamManagementService extends AbstractTeamManagementService {

    @Autowired
    private ProcessRoleService processRoleService;

    public ApplicationTeamManagementViewModel createViewModel(long applicationId, long organisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                applicationId, organisationId, loggedInUser.getId());
    }

    public ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                       long organisationId,
                                                                       ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = mapStagedInviteToInviteResource(form, applicationId, organisationId);
        return inviteRestService.createInvitesByOrganisationForApplication(applicationId, organisationId, singletonList(invite)).toServiceResult();
    }

    @Override
    public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationId) {

        return hasExistingOrganisationInvite(applicationId, organisationId) ||
               hasExistingUsersOnApplicationUsingThisOrganisation(applicationId, organisationId);
    }

    private boolean hasExistingUsersOnApplicationUsingThisOrganisation(Long applicationId, Long organisationId) {

        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        return simpleAnyMatch(processRoles, processRole -> processRole.getOrganisationId().equals(organisationId));
    }

    private boolean hasExistingOrganisationInvite(Long applicationId, Long organisationId) {
        Optional<InviteOrganisationResource> organisationInvite =
                inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId).
                        toOptionalIfNotFound().
                        getSuccess();

        return organisationInvite.isPresent();
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
