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
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
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

    public ApplicationTeamManagementViewModel populateModel(long applicationId, long organisationId, long loggedInUserId) {
        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(organisationId, leadOrganisationResource);
        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation,
                organisationInviteSupplier(applicationId, organisationId));
    }

    public ApplicationTeamManagementViewModel populateModel(long applicationId, String organisationName, long loggedInUserId) {
        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(organisationName, leadOrganisationResource);
        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation,
                organisationInviteSupplier(applicationId, organisationName));
    }

    private ApplicationTeamManagementViewModel populateModel(long applicationId, long loggedInUserId,
                                                             OrganisationResource leadOrganisationResource,
                                                             boolean requestForLeadOrganisation,
                                                             Supplier<Optional<InviteOrganisationResource>> inviteOrganisationSupplier) {
        Optional<InviteOrganisationResource> inviteOrganisationResourceOptional = inviteOrganisationSupplier.get();
        List<ApplicationInviteResource> invites = inviteOrganisationResourceOptional.map(InviteOrganisationResource::getInviteResources).orElse(emptyList());
        if (requestForLeadOrganisation) {
            return populateModelForLeadOrganisation(applicationId, loggedInUserId, leadOrganisationResource.getId(), leadOrganisationResource.getName(), invites);
        }
        return inviteOrganisationResourceOptional.map(inviteOrganisationResource ->
                populateModelForNonLeadOrganisation(applicationId, loggedInUserId, inviteOrganisationResource))
                .orElseThrow(() -> new ObjectNotFoundException("Organisation invite not found", null));
    }

    private ApplicationTeamManagementViewModel populateModelForLeadOrganisation(long applicationId, long loggedInUserId,
                                                                                long organisationId, String organisationName,
                                                                                List<ApplicationInviteResource> applicationInviteResources) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getApplicationDisplayName(),
                organisationId,
                organisationName,
                true,
                isUserLeadApplicant(loggedInUserId, leadApplicant),
                combineLists(getLeadApplicantViewModel(leadApplicant), simpleMap(applicationInviteResources,
                        applicationInviteResource -> getApplicantViewModel(applicationInviteResource, loggedInUserId)))
        );
    }

    private ApplicationTeamManagementViewModel populateModelForNonLeadOrganisation(long applicationId, long loggedInUserId,
                                                                                   InviteOrganisationResource inviteOrganisationResource) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getApplicationDisplayName(),
                inviteOrganisationResource.getOrganisation(),
                getOrganisationName(inviteOrganisationResource),
                false,
                isUserLeadApplicant(loggedInUserId, leadApplicant),
                simpleMap(inviteOrganisationResource.getInviteResources(), applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, loggedInUserId)));
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource, long loggedInUserId) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        boolean removable = applicationInviteResource.getUser() == null || applicationInviteResource.getUser() != loggedInUserId;
        return new ApplicationTeamManagementApplicantRowViewModel(applicationInviteResource.getId(), getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending, removable);
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

    private Supplier<Optional<InviteOrganisationResource>> organisationInviteSupplier(long applicationId, long organisationId) {
        return () -> getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getOrganisation() != null && inviteOrganisationResource.getOrganisation() == organisationId).findFirst())
                .getSuccessObjectOrThrowException();
    }

    private Supplier<Optional<InviteOrganisationResource>> organisationInviteSupplier(long applicationId, String organisationName) {
        return () -> getOrganisationInvites(applicationId).andOnSuccessReturn(inviteOrganisationResources ->
                inviteOrganisationResources.stream().filter(inviteOrganisationResource ->
                        inviteOrganisationResource.getOrganisationName().equals(organisationName)).findFirst())
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

    private boolean isRequestForLeadOrganisation(String requestedOrganisationName, OrganisationResource leadOrganisationResource) {
        return requestedOrganisationName.equals(leadOrganisationResource.getName());
    }
}
