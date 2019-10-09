package org.innovateuk.ifs.projectteam.util;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * A helper class to handle project invites, sent by either internal or external users
 */
@Component
public class ProjectInviteHelper {

    private final ProjectDetailsService projectDetailsService;

    private final ProjectService projectService;

    private final OrganisationRestService organisationRestService;

    public ProjectInviteHelper(ProjectDetailsService projectDetailsService,
                               ProjectService projectService,
                               OrganisationRestService organisationRestService) {
        this.projectDetailsService = projectDetailsService;
        this.projectService = projectService;
        this.organisationRestService = organisationRestService;
    }

    public String sendInvite(String inviteName,
                             String inviteEmail,
                             UserResource loggedInUser,
                             ValidationHandler validationHandler,
                             Supplier<String> failureView,
                             Supplier<String> successView,
                             long projectId,
                             long organisation,
                             BiFunction<Long, ProjectUserInviteResource, ServiceResult<Void>> sendInvite) {

            validateIfTryingToInviteSelf(loggedInUser.getEmail(), inviteEmail, validationHandler);
            ProjectUserInviteResource invite = createProjectInviteResourceForNewContact(projectId, inviteName, inviteEmail, organisation);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> inviteResult = sendInvite.apply(projectId, invite);
                return validationHandler.addAnyErrors(inviteResult).failNowOrSucceedWith(failureView, successView);
            });
    }

    private void validateIfTryingToInviteSelf(String loggedInUserEmail,
                                              String inviteEmail,
                                              ValidationHandler validationHandler) {
        if (equalsIgnoreCase(loggedInUserEmail, inviteEmail)) {
            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.PROJECT_SETUP_CANNOT_INVITE_SELF));
        }
    }

    private ProjectUserInviteResource createProjectInviteResourceForNewContact(long projectId,
                                                                               String name,
                                                                               String email,
                                                                               long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ProjectUserInviteResource inviteResource = new ProjectUserInviteResource();

        inviteResource.setProject(projectId);
        inviteResource.setName(name);
        inviteResource.setEmail(email);
        inviteResource.setOrganisation(organisationId);
        inviteResource.setOrganisationName(organisationResource.getName());
        inviteResource.setApplicationId(projectResource.getApplication());
        inviteResource.setLeadOrganisationId(leadOrganisation.getId());

        return inviteResource;
    }

    public void resendInvite(long id, long projectId, BiFunction<Long, ProjectUserInviteResource, ServiceResult<Void>> sendInvite) {
        Optional<ProjectUserInviteResource> existingInvite = projectDetailsService
                .getInvitesByProject(projectId)
                .getSuccess()
                .stream()
                .filter(i -> id ==(i.getId()))
                .findFirst();

        existingInvite
                .ifPresent(i -> sendInvite.apply(projectId, existingInvite.get()).getSuccess());
    }
}
