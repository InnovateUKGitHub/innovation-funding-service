package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
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
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the model for the Application Team Update view.
 */
@Component
public class ApplicationTeamManagementModelPopulator {

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

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
        InviteOrganisationResource inviteOrganisationResource = getInviteOrganisationByInviteOrganisationId(inviteOrganisationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(inviteOrganisationResource, leadOrganisationResource);

        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModel(long applicationId,
                                                             long loggedInUserId,
                                                             OrganisationResource leadOrganisationResource,
                                                             boolean requestForLeadOrganisation,
                                                             InviteOrganisationResource inviteOrganisationResource) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);

        if (requestForLeadOrganisation) {
            return populateModelForLeadOrganisation(leadOrganisationResource.getId(), leadOrganisationResource.getName(),
                    applicationResource, leadApplicant, userLeadApplicant, inviteOrganisationResource);
        }

        return populateModelForNonLeadOrganisation(applicationResource, userLeadApplicant, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModelForLeadOrganisation(long organisationId, String organisationName,
                                                                                ApplicationResource applicationResource,
                                                                                UserResource leadApplicant,
                                                                                boolean userLeadApplicant,
                                                                                InviteOrganisationResource inviteOrganisationResource) {
        List<ApplicationInviteResource> invites = ofNullable(inviteOrganisationResource)
                .map(InviteOrganisationResource::getInviteResources).orElse(emptyList());

        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getName(),
                organisationId,
                ofNullable(inviteOrganisationResource).map(InviteOrganisationResource::getId).orElse(null),
                organisationName,
                true,
                userLeadApplicant,
                combineLists(getLeadApplicantViewModel(leadApplicant), simpleMap(invites, applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, userLeadApplicant)))
        );
    }

    private ApplicationTeamManagementViewModel populateModelForNonLeadOrganisation(ApplicationResource applicationResource,
                                                                                   boolean userLeadApplicant,
                                                                                   InviteOrganisationResource inviteOrganisationResource) {
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                applicationResource.getName(),
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
        return inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
                .toOptionalIfNotFound().getSuccessObjectOrThrowException();
    }

    private InviteOrganisationResource getInviteOrganisationByInviteOrganisationId(long inviteOrganisationId) {
        return inviteOrganisationRestService.getById(inviteOrganisationId)
                .getSuccessObjectOrThrowException();
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
