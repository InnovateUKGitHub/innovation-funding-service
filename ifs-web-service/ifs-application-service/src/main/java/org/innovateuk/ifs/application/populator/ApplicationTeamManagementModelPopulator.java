package org.innovateuk.ifs.application.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementOrganisationViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
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

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Application Team Management view.
 */
@Component
public class ApplicationTeamManagementModelPopulator {

    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganisationService organisationService;

    public ApplicationTeamManagementViewModel populateModel(Long applicationId, Long organisationId, UserResource user, Long authenticatedUserOrganisationId, long selectedOrgIndex) {
        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());
        OrganisationResource selectedOrganisation = organisationId != null ? organisationService.getOrganisationById(organisationId) : null;

        return new ApplicationTeamManagementViewModel(application.getId(), application.getApplicationDisplayName(), leadApplicant,
                leadOrganisation, selectedOrganisation, user, authenticatedUserOrganisationId,
                getOrganisationViewModel(application, selectedOrganisation.getId()), selectedOrgIndex);
    }

    private ApplicationTeamManagementOrganisationViewModel getOrganisationViewModel(ApplicationResource application, Long selectedOrgId) {
        OrganisationResource leadOrganisation = getLeadOrganisation(application);

        List<InviteOrganisationResource> inviteOrganisationResources = getOrganisationInvites(application, leadOrganisation.getId());

        List<ApplicationTeamManagementOrganisationViewModel> organisationRowViewModelsForInvites = inviteOrganisationResources
                .stream().map(inviteOrganisationResource -> getOrganisationViewModel(inviteOrganisationResource,
                        leadOrganisation.getId())).collect(toList());

        if (!organisationInvitesContainsLeadOrganisation(inviteOrganisationResources, leadOrganisation.getId())) {
            organisationRowViewModelsForInvites = appendLeadOrganisation(organisationRowViewModelsForInvites, leadOrganisation);
        }

        List<ApplicationTeamManagementOrganisationViewModel> allOrganisations = appendLeadApplicant(organisationRowViewModelsForInvites, application);

        return allOrganisations.stream().filter(org -> org.getId() == selectedOrgId).findFirst().get();
    }

    private boolean organisationInvitesContainsLeadOrganisation(List<InviteOrganisationResource> inviteOrganisationResources,
                                                                long leadOrganisationId) {
        return inviteOrganisationResources.stream().anyMatch(inviteOrganisationResource ->
                inviteOrganisationResource.getOrganisation() == leadOrganisationId);
    }

    private List<InviteOrganisationResource> getOrganisationInvites(ApplicationResource applicationResource, long leadOrganisationId) {
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(
                applicationResource.getId()).handleSuccessOrFailure(failure -> Collections.emptyList(), success -> success);
        inviteOrganisationResources.sort(compareByLeadOrganisationThenById(leadOrganisationId));
        return inviteOrganisationResources;
    }

    private List<ApplicationTeamManagementOrganisationViewModel> appendLeadOrganisation(
            List<ApplicationTeamManagementOrganisationViewModel> organisationRowViewModels, OrganisationResource leadOrganisation) {
        organisationRowViewModels.add(0, new ApplicationTeamManagementOrganisationViewModel(leadOrganisation.getId(),
                leadOrganisation.getName(), true, new ArrayList<>()));
        return organisationRowViewModels;
    }

    private List<ApplicationTeamManagementOrganisationViewModel> appendLeadApplicant(
                List<ApplicationTeamManagementOrganisationViewModel> organisationRowViewModels,
                ApplicationResource applicationResource) {

        organisationRowViewModels.stream().filter(ApplicationTeamManagementOrganisationViewModel::isLead).findFirst().ifPresent(
                applicationTeamOrganisationRowViewModel -> applicationTeamOrganisationRowViewModel.getApplicants().add(0,
                        getLeadApplicantViewModel(getLeadApplicant(applicationResource))));
        return organisationRowViewModels;
    }

    private ApplicationTeamManagementOrganisationViewModel getOrganisationViewModel(InviteOrganisationResource inviteOrganisationResource, long leadOrganisationId) {
        boolean lead = leadOrganisationId == inviteOrganisationResource.getOrganisation();
        return new ApplicationTeamManagementOrganisationViewModel(inviteOrganisationResource.getOrganisation(),
                getOrganisationName(inviteOrganisationResource), lead, inviteOrganisationResource.getInviteResources()
                .stream().map(this::getApplicantViewModel).collect(toList()));
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamManagementApplicantRowViewModel(applicationInviteResource.getUser(), getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending);
    }

    private ApplicationTeamManagementApplicantRowViewModel getLeadApplicantViewModel(UserResource userResource) {
        return new ApplicationTeamManagementApplicantRowViewModel(userResource.getId(), userResource.getName(), userResource.getEmail(), true, false);
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return StringUtils.isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return StringUtils.isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(ApplicationResource application) {
        //return applicationService.getLeadOrganisation(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return  organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());

    }

    private List<InviteOrganisationResource> getSavedInviteOrganisations(ApplicationResource application) {
        return inviteRestService.getInvitesByApplication(application.getId()).handleSuccessOrFailure(
                failure -> Collections.<InviteOrganisationResource>emptyList(),
                success -> success);
    }

    private Long getAuthenticatedUserOrganisationId(UserResource user, List<InviteOrganisationResource> savedInvites) {
        Optional<InviteOrganisationResource> matchingOrganisationResource = savedInvites.stream()
                .filter(inviteOrg -> inviteOrg.getInviteResources().stream()
                        .anyMatch(inv -> user.getEmail().equals(inv.getEmail())))
                .findFirst();
        return matchingOrganisationResource.map((invOrgRes -> invOrgRes.getOrganisation())).orElse(null);
    }

    private static Comparator<? super InviteOrganisationResource> compareByLeadOrganisationThenById(long leadOrganisationId) {
        return (organisation1, organisation2) -> organisation1.getOrganisation() == leadOrganisationId ? -1 :
                organisation2.getOrganisation() == leadOrganisationId ? 1 :
                        organisation1.getId().compareTo(organisation2.getId());
    }
}
