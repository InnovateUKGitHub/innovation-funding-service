package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/application/{applicationId}/team/update/existing/{organisationId}")
public class ApplicationExistingTeamManagementController extends AbstractApplicationTeamManagementController {

    @Autowired
    private ProcessRoleService processRoleService;

    protected ApplicationTeamManagementViewModel createViewModel(long applicationId, long organisationId, UserResource loggedInUser) {
        return applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                applicationId, organisationId, loggedInUser.getId());
    }

    protected ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                     long organisationId,
                                                                     ApplicationTeamUpdateForm form) {
        ApplicationInviteResource invite = createInvite(form, applicationId, organisationId);
        return inviteRestService.createInvitesByOrganisationForApplication(applicationId, organisationId, Arrays.asList(invite)).toServiceResult();
    }

    protected String validateOrganisationAndApplicationIds(Long applicationId, Long organisationId, Supplier<String> supplier) {
        List<ProcessRoleResource> processRoles = processRoleService.getByApplicationId(applicationId);
        if (processRoles.stream().anyMatch(processRoleResource -> organisationId.equals(processRoleResource.getOrganisationId()))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation id not found in application id provided.", Collections.emptyList());
    }

    protected List<Long> getInviteIds(long applicationId, long organisationId) {
        return inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
                .toOptionalIfNotFound()
                .getSuccessObjectOrThrowException()
                .map(organisation -> organisation.getInviteResources().stream()
                        .map(ApplicationInviteResource::getId)
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }
}
