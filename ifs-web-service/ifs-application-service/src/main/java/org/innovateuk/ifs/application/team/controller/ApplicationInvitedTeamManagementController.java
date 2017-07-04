package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update/invited/{organisationId}")
public class ApplicationInvitedTeamManagementController extends AbstractApplicationTeamManagementController {


    protected ApplicationTeamManagementViewModel createViewModel(long applicationId, long inviteOrganisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(
                applicationId, inviteOrganisationId, loggedInUser.getId());
    }

    protected ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                       long inviteOrganisationId,
                                                                       ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = createInvite(form, applicationId, inviteOrganisationId);

        return inviteRestService.saveInvites(Arrays.asList(invite)).toServiceResult();
    }

    protected String validateOrganisationAndApplicationIds(Long applicationId, Long organisationInviteId, Supplier<String> supplier) {
        InviteOrganisationResource organisation = inviteOrganisationRestService.getById(organisationInviteId).getSuccessObjectOrThrowException();
        if(organisation.getInviteResources().stream().anyMatch(applicationInviteResource -> applicationInviteResource.getApplication().equals(applicationId))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation invite id not found in application id provided.", Collections.emptyList());
    }

    protected List<Long> getInviteIds(long applicationId, long organisationId) {
        return inviteOrganisationRestService.getById(organisationId)
                .getSuccessObjectOrThrowException()
                .getInviteResources().stream()
                .map(ApplicationInviteResource::getId)
                .collect(Collectors.toList());
    }
}
