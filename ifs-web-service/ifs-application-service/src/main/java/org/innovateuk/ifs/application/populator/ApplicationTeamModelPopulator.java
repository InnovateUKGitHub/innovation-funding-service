package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamOrganisationRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Builds the model for the Application Team view.
 */
@Component
public class ApplicationTeamModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserService userService;

    public ApplicationTeamViewModel populateModel(long applicationId, long loggedInUserId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userIsLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);
        boolean applicationCanBegin = isApplicationStatusCreated(applicationResource) && userIsLeadApplicant;
        return new ApplicationTeamViewModel(applicationResource.getId(), applicationResource.getName(),
                getOrganisationViewModels(applicationResource.getId(), loggedInUserId, leadApplicant),
                userIsLeadApplicant, applicationCanBegin);
    }

    private boolean isApplicationStatusCreated(ApplicationResource applicationResource) {
        return ApplicationStatusConstants.CREATED == ApplicationStatusConstants.getFromId(applicationResource.getApplicationStatus());
    }

    private List<ApplicationTeamOrganisationRowViewModel> getOrganisationViewModels(long applicationId, long loggedInUserId,
                                                                                    UserResource leadApplicant) {
        OrganisationResource leadOrganisation = getLeadOrganisation(applicationId);

        List<InviteOrganisationResource> inviteOrganisationResources = getOrganisationInvites(applicationId, leadOrganisation.getId());

        boolean userLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);

        List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModelsForInvites = inviteOrganisationResources.stream()
                .map(inviteOrganisationResource -> getOrganisationViewModel(inviteOrganisationResource, leadOrganisation.getId(),
                        loggedInUserId, userLeadApplicant)).collect(toList());

        if (isNoInvitesForLeadOrganisation(inviteOrganisationResources, leadOrganisation.getId())) {
            organisationRowViewModelsForInvites = appendLeadOrganisation(organisationRowViewModelsForInvites, leadOrganisation, userLeadApplicant);
        }

        return appendLeadApplicant(organisationRowViewModelsForInvites, leadApplicant);
    }

    private boolean isNoInvitesForLeadOrganisation(List<InviteOrganisationResource> inviteOrganisationResources,
                                                   long leadOrganisationId) {
        return inviteOrganisationResources.stream().noneMatch(inviteOrganisationResource ->
                isInviteForOrganisation(inviteOrganisationResource, leadOrganisationId));
    }

    private List<ApplicationTeamOrganisationRowViewModel> appendLeadOrganisation(
            List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModels, OrganisationResource leadOrganisation,
            boolean editable) {
        // The InviteOrganisation id is null since there are no invites
        Long inviteOrganisationId = null;
        organisationRowViewModels.add(0, new ApplicationTeamOrganisationRowViewModel(leadOrganisation.getId(), inviteOrganisationId,
                leadOrganisation.getName(), true, new ArrayList<>(), editable));
        return organisationRowViewModels;
    }

    private List<ApplicationTeamOrganisationRowViewModel> appendLeadApplicant(
            List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModels, UserResource leadApplicant) {
        organisationRowViewModels.stream().filter(ApplicationTeamOrganisationRowViewModel::isLead).findFirst().ifPresent(
                applicationTeamOrganisationRowViewModel -> applicationTeamOrganisationRowViewModel.getApplicants().add(0,
                        getLeadApplicantViewModel(leadApplicant)));
        return organisationRowViewModels;
    }

    private ApplicationTeamOrganisationRowViewModel getOrganisationViewModel(InviteOrganisationResource inviteOrganisationResource,
                                                                             long leadOrganisationId, long loggedInUserId,
                                                                             boolean userLeadApplicant) {
        boolean leadOrganisation = isInviteForOrganisation(inviteOrganisationResource, leadOrganisationId);
        boolean editable = userLeadApplicant || isUserMemberOfOrganisation(loggedInUserId, inviteOrganisationResource);
        return new ApplicationTeamOrganisationRowViewModel(inviteOrganisationResource.getOrganisation(),
                inviteOrganisationResource.getId(), getOrganisationName(inviteOrganisationResource), leadOrganisation,
                inviteOrganisationResource.getInviteResources().stream().map(this::getApplicantViewModel).collect(toList()), editable);
    }

    private ApplicationTeamApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamApplicantRowViewModel(getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending);
    }

    private ApplicationTeamApplicantRowViewModel getLeadApplicantViewModel(UserResource leadApplicant) {
        return new ApplicationTeamApplicantRowViewModel(leadApplicant.getName(), leadApplicant.getEmail(), true, false);
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private List<InviteOrganisationResource> getOrganisationInvites(long applicationId, long leadOrganisationId) {
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(
                applicationId).handleSuccessOrFailure(failure -> emptyList(), success -> success);
        return sortOrganisationInvitesByLeadOrganisationThenByName(inviteOrganisationResources, leadOrganisationId);
    }

    private List<InviteOrganisationResource> sortOrganisationInvitesByLeadOrganisationThenByName(
            List<InviteOrganisationResource> inviteOrganisationResources, long leadOrganisationId) {

        Optional<InviteOrganisationResource> leadOrganisationInvite = simpleFindFirst(inviteOrganisationResources, inviteOrganisationResource ->
                isInviteForOrganisation(inviteOrganisationResource, leadOrganisationId));
        return new PrioritySorting<>(inviteOrganisationResources, leadOrganisationInvite.orElse(null), this::getOrganisationName).unwrap();
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }

    private boolean isUserLeadApplicant(long userId, UserResource leadApplicant) {
        return leadApplicant.getId().equals(userId);
    }

    private boolean isInviteForOrganisation(InviteOrganisationResource inviteOrganisationResource, long organisationId) {
        return inviteOrganisationResource.getOrganisation() != null && inviteOrganisationResource.getOrganisation().equals(organisationId);
    }

    private boolean isUserMemberOfOrganisation(long userId, InviteOrganisationResource inviteOrganisationResource) {
        return inviteOrganisationResource.getInviteResources().stream().anyMatch(applicationInviteResource ->
                applicationInviteResource.getUser() != null && applicationInviteResource.getUser().equals(userId));
    }
}