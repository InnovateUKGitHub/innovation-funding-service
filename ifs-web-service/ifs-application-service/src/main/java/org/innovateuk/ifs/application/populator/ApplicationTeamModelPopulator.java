package org.innovateuk.ifs.application.populator;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Application Team view.
 */
@Component
public class ApplicationTeamModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserService userService;

    public ApplicationTeamViewModel populateModel(long applicationId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        return new ApplicationTeamViewModel(applicationResource.getId(), applicationResource.getApplicationDisplayName(),
                getOrganisationViewModels(applicationResource));
    }

    private List<ApplicationTeamOrganisationRowViewModel> getOrganisationViewModels(ApplicationResource applicationResource) {
        //savedInvites.sort(comparing(InviteOrganisationResource::getId));
        // make sure the lead organisation is the first organisation
        // The lead organisation may(?) have no invites, if so make sure the lead organisation is added with the lead applicant

        return getOrganisationInvites(applicationResource).stream().map(this::getOrganisationViewModel).collect(toList());
    }

    private ApplicationTeamOrganisationRowViewModel getOrganisationViewModel(InviteOrganisationResource inviteOrganisationResource) {
        return new ApplicationTeamOrganisationRowViewModel(
                getOrganisationName(inviteOrganisationResource),
                false, inviteOrganisationResource.getInviteResources().stream().map(this::getApplicantViewModel).collect(toList()));
    }

    private ApplicationTeamApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean lead = false;
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamApplicantRowViewModel(getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), lead, pending);
    }

    private UserResource getLeadApplicant(ApplicationResource application) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private List<InviteOrganisationResource> getOrganisationInvites(ApplicationResource applicationResource) {
        return inviteRestService.getInvitesByApplication(
                applicationResource.getId()).handleSuccessOrFailure(failure -> Collections.emptyList(), success -> success);
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return StringUtils.isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return StringUtils.isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }
}