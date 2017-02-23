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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        OrganisationResource leadOrganisation = getLeadOrganisation(applicationResource.getId());

        List<InviteOrganisationResource> inviteOrganisationResources = getOrganisationInvites(
                applicationResource, leadOrganisation.getId());

        List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModelsForInvites = inviteOrganisationResources
                .stream().map(inviteOrganisationResource -> getOrganisationViewModel(inviteOrganisationResource,
                        leadOrganisation.getId())).collect(toList());

        if (!organisationInvitesContainsLeadOrganisation(
                inviteOrganisationResources, leadOrganisation.getId())) {
            organisationRowViewModelsForInvites = appendLeadOrganisation(organisationRowViewModelsForInvites, leadOrganisation);
        }

        return appendLeadApplicant(organisationRowViewModelsForInvites, applicationResource);
    }

    private boolean organisationInvitesContainsLeadOrganisation(List<InviteOrganisationResource> inviteOrganisationResources,
                                                                long leadOrganisationId) {
        return inviteOrganisationResources.stream().anyMatch(inviteOrganisationResource ->
                inviteOrganisationResource.getOrganisation() == leadOrganisationId);
    }

    private List<ApplicationTeamOrganisationRowViewModel> appendLeadOrganisation(
            List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModels, OrganisationResource leadOrganisation) {
        organisationRowViewModels.add(0, new ApplicationTeamOrganisationRowViewModel(leadOrganisation.getId(),
                leadOrganisation.getName(), true, new ArrayList<>()));
        return organisationRowViewModels;
    }

    private List<ApplicationTeamOrganisationRowViewModel> appendLeadApplicant(
            List<ApplicationTeamOrganisationRowViewModel> organisationRowViewModels, ApplicationResource applicationResource) {
        organisationRowViewModels.stream().filter(ApplicationTeamOrganisationRowViewModel::isLead).findFirst().ifPresent(
                applicationTeamOrganisationRowViewModel -> applicationTeamOrganisationRowViewModel.getApplicants().add(0,
                        getLeadApplicantViewModel(getLeadApplicant(applicationResource))));
        return organisationRowViewModels;
    }

    private ApplicationTeamOrganisationRowViewModel getOrganisationViewModel(InviteOrganisationResource inviteOrganisationResource, long leadOrganisationId) {
        boolean lead = leadOrganisationId == inviteOrganisationResource.getOrganisation();
        return new ApplicationTeamOrganisationRowViewModel(inviteOrganisationResource.getOrganisation(),
                getOrganisationName(inviteOrganisationResource), lead, inviteOrganisationResource.getInviteResources()
                .stream().map(this::getApplicantViewModel).collect(toList()));
    }

    private ApplicationTeamApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamApplicantRowViewModel(getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending);
    }

    private ApplicationTeamApplicantRowViewModel getLeadApplicantViewModel(UserResource userResource) {
        return new ApplicationTeamApplicantRowViewModel(userResource.getName(), userResource.getEmail(), true, false);
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private List<InviteOrganisationResource> getOrganisationInvites(ApplicationResource applicationResource, long leadOrganisationId) {
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(
                applicationResource.getId()).handleSuccessOrFailure(failure -> Collections.emptyList(), success -> success);
        inviteOrganisationResources.sort(compareByLeadOrganisationThenById(leadOrganisationId));
        return inviteOrganisationResources;
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return StringUtils.isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return StringUtils.isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }

    private static Comparator<? super InviteOrganisationResource> compareByLeadOrganisationThenById(long leadOrganisationId) {
        return (organisation1, organisation2) -> organisation1.getOrganisation() == leadOrganisationId ? -1 :
                organisation2.getOrganisation() == leadOrganisationId ? 1 :
                        organisation1.getId().compareTo(organisation2.getId());
    }
}