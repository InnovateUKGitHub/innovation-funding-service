package org.innovateuk.ifs.application.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Application Team Update view.
 */
@Component
public class ApplicationTeamManagementModelPopulator {

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    public ApplicationTeamManagementViewModel populateModel(long applicationId, String organisationName, long loggedInUserId) {
        InviteOrganisationResource inviteOrganisationResource = getOrganisationInvite(applicationId, organisationName);
        return populateModel(applicationId, loggedInUserId, inviteOrganisationResource);
    }

    public ApplicationTeamManagementViewModel populateModel(long applicationId, long organisationId, long loggedInUserId) {
        InviteOrganisationResource inviteOrganisationResource = getOrganisationInvite(applicationId, organisationId);
        return populateModel(applicationId, loggedInUserId, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModel(long applicationId, long loggedInUserId, InviteOrganisationResource inviteOrganisationResource) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean leadOrganisation = isInviteForLeadOrganisation(inviteOrganisationResource, leadOrganisationResource.getId());
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userIsLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);
        List<ApplicationTeamManagementApplicantRowViewModel> applicantRowViewModelsForInvites = inviteOrganisationResource.getInviteResources()
                .stream().map(this::getApplicantViewModel).collect(toList());

        if (leadOrganisation) {
            applicantRowViewModelsForInvites = appendLeadApplicant(applicantRowViewModelsForInvites, leadApplicant);
        }

        return new ApplicationTeamManagementViewModel(applicationResource.getId(), applicationResource.getApplicationDisplayName(),
                inviteOrganisationResource.getOrganisation(), getOrganisationName(inviteOrganisationResource),
                leadOrganisation, userIsLeadApplicant, applicantRowViewModelsForInvites);
    }

    private List<ApplicationTeamManagementApplicantRowViewModel> appendLeadApplicant(
            List<ApplicationTeamManagementApplicantRowViewModel> applicantRowViewModels, UserResource leadApplicant) {
        applicantRowViewModels.add(0, getLeadApplicantViewModel(leadApplicant));
        return applicantRowViewModels;
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamManagementApplicantRowViewModel(applicationInviteResource.getUser(), getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending);
    }

    private ApplicationTeamManagementApplicantRowViewModel getLeadApplicantViewModel(UserResource userResource) {
        return new ApplicationTeamManagementApplicantRowViewModel(userResource.getId(), userResource.getName(), userResource.getEmail(), true, false);
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private InviteOrganisationResource getOrganisationInvite(long applicationId, long organisationId) {
        return getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getOrganisation() != null && inviteOrganisationResource.getOrganisation() == organisationId).findFirst().get())
                .getSuccessObjectOrThrowException();
    }

    private InviteOrganisationResource getOrganisationInvite(long applicationId, String organisationName) {
        return getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getOrganisationName().equals(organisationName)).findFirst().get())
                .getSuccessObjectOrThrowException();
    }

    private ServiceResult<List<InviteOrganisationResource>> getOrganisationInvites(long applicationId) {
        return inviteRestService.getInvitesByApplication(applicationId).toServiceResult();
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return StringUtils.isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return StringUtils.isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }

    private boolean isUserLeadApplicant(long userId, UserResource leadApplicant) {
        return userId == leadApplicant.getId();
    }

    private boolean isInviteForLeadOrganisation(InviteOrganisationResource inviteOrganisationResource, long leadOrganisationId) {
        return inviteOrganisationResource.getOrganisation() != null && inviteOrganisationResource.getOrganisation() == leadOrganisationId;
    }
}
