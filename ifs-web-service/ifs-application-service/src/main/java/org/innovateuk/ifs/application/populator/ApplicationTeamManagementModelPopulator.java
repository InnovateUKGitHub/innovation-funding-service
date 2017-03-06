package org.innovateuk.ifs.application.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
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
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

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

    public ApplicationTeamManagementViewModel populateModelByOrganisationId(long applicationId, long organisationId, long loggedInUserId) {
        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(organisationId, leadOrganisationResource);
        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation,
                getInviteOrganisationByOrganisationId(applicationId, organisationId).orElse(null));
    }

    public ApplicationTeamManagementViewModel populateModelByInviteOrganisationId(long applicationId, long inviteOrganisationId, long loggedInUserId) {
        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        Optional<InviteOrganisationResource> inviteOrganisationResource = getInviteOrganisationByInviteOrganisationId(applicationId, inviteOrganisationId);
        boolean requestForLeadOrganisation = inviteOrganisationResource.map(inviteOrganisationResourceValue ->
                isRequestForLeadOrganisation(inviteOrganisationResourceValue, leadOrganisationResource)).orElse(false);
        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation,
                inviteOrganisationResource.orElse(null));
    }

    private ApplicationTeamManagementViewModel populateModel(long applicationId, long loggedInUserId,
                                                             OrganisationResource leadOrganisationResource,
                                                             boolean requestForLeadOrganisation,
                                                             InviteOrganisationResource inviteOrganisationResource) {
        if (requestForLeadOrganisation) {
            return populateModelForLeadOrganisation(applicationId, loggedInUserId, leadOrganisationResource.getId(), leadOrganisationResource.getName(), inviteOrganisationResource);
        }
        return populateModelForNonLeadOrganisation(applicationId, loggedInUserId, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModelForLeadOrganisation(long applicationId, long loggedInUserId,
                                                                                long organisationId, String organisationName,
                                                                                InviteOrganisationResource inviteOrganisationResource) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        // The InviteOrganisation id is null since there are no invites
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);
        List<ApplicationInviteResource> invites = ofNullable(inviteOrganisationResource)
                .map(InviteOrganisationResource::getInviteResources).orElse(emptyList());
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getApplicationDisplayName(),
                organisationId,
                ofNullable(inviteOrganisationResource).map(InviteOrganisationResource::getId).orElse(null),
                organisationName,
                true,
                userLeadApplicant,
                combineLists(getLeadApplicantViewModel(leadApplicant), simpleMap(invites, applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, userLeadApplicant)))
        );
    }

    private ApplicationTeamManagementViewModel populateModelForNonLeadOrganisation(long applicationId, long loggedInUserId,
                                                                                   InviteOrganisationResource inviteOrganisationResource) {
        if (inviteOrganisationResource == null) {
            throw new ObjectNotFoundException("Organisation invite not found", null);
        }
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getApplicationDisplayName(),
                inviteOrganisationResource.getOrganisation(),
                inviteOrganisationResource.getId(),
                getOrganisationName(inviteOrganisationResource),
                false,
                userLeadApplicant,
                simpleMap(inviteOrganisationResource.getInviteResources(), applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, userLeadApplicant)));
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource, boolean userLeadApplicant) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamManagementApplicantRowViewModel(applicationInviteResource.getId(), getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending, userLeadApplicant);
    }

    private ApplicationTeamManagementApplicantRowViewModel getLeadApplicantViewModel(UserResource leadApplicant) {
        return new ApplicationTeamManagementApplicantRowViewModel(leadApplicant.getName(), leadApplicant.getEmail(), true, false, false);
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private Optional<InviteOrganisationResource> getInviteOrganisationByOrganisationId(long applicationId, long organisationId) {
        return getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getOrganisation() != null && inviteOrganisationResource.getOrganisation() == organisationId).findFirst())
                .getSuccessObjectOrThrowException();
    }

    private Optional<InviteOrganisationResource> getInviteOrganisationByInviteOrganisationId(long applicationId, long inviteOrganisationId) {
        return getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getId().equals(inviteOrganisationId)).findFirst())
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

    private boolean isRequestForLeadOrganisation(long requestedOrganisationId, OrganisationResource leadOrganisationResource) {
        return requestedOrganisationId == leadOrganisationResource.getId();
    }

    private boolean isRequestForLeadOrganisation(InviteOrganisationResource inviteOrganisationResource, OrganisationResource leadOrganisationResource) {
        return inviteOrganisationResource.getOrganisation() != null
                && inviteOrganisationResource.getOrganisation().equals(leadOrganisationResource.getId());
    }
}
