package org.innovateuk.ifs.application.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamManagementOrganisationRowViewModel;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public ApplicationTeamManagementViewModel populateModel(Long applicationId, Long organisationId, UserResource user, Long authenticatedUserOrganisationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());
        OrganisationResource selectedOrganisation = organisationId != null ? organisationService.getOrganisationById(organisationId) : null;

    /*    List<InviteOrganisationResource> savedInvites = getSavedInviteOrganisations(application);
        Map<Long, InviteOrganisationResource> organisationInvites = savedInvites.stream().collect(Collectors.toMap(InviteOrganisationResource::getId, Function.identity()));

        Long authenticatedUserOrganisationId = getAuthenticatedUserOrganisationId(user, savedInvites);

        model.addAttribute("authenticatedUser", user);
        model.addAttribute("authenticatedUserOrganisation", authenticatedUserOrganisationId);
        model.addAttribute("currentApplication", application);
      //  model.addAttribute("leadApplicant", leadApplicant);
        model.addAttribute("leadOrganisation", leadOrganisation);
        model.addAttribute("organisationInvites", organisationInvites);
        if (organisationId != null) {
            OrganisationResource selectedOrganisation = organisationService.getOrganisationById(organisationId);
            model.addAttribute("selectedOrganisation", selectedOrganisation);
        }*/

        return new ApplicationTeamManagementViewModel(application.getId(), application.getApplicationDisplayName(), leadApplicant,
                leadOrganisation, selectedOrganisation, user, authenticatedUserOrganisationId,
                getOrganisationViewModels(application));
    }

    private List<ApplicationTeamManagementOrganisationRowViewModel> getOrganisationViewModels(ApplicationResource application) {
        OrganisationResource leadOrganisation = getLeadOrganisation(application);

        List<InviteOrganisationResource> inviteOrganisationResources = getOrganisationInvites(application, leadOrganisation.getId());

        List<ApplicationTeamManagementOrganisationRowViewModel> organisationRowViewModelsForInvites = inviteOrganisationResources
                .stream().map(inviteOrganisationResource -> getOrganisationViewModel(inviteOrganisationResource,
                        leadOrganisation.getId())).collect(toList());

        if (!organisationInvitesContainsLeadOrganisation(
                inviteOrganisationResources, leadOrganisation.getId())) {
            organisationRowViewModelsForInvites = appendLeadOrganisation(organisationRowViewModelsForInvites, leadOrganisation);
        }

        return appendLeadApplicant(organisationRowViewModelsForInvites, application);
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

    private List<ApplicationTeamManagementOrganisationRowViewModel> appendLeadOrganisation(
            List<ApplicationTeamManagementOrganisationRowViewModel> organisationRowViewModels, OrganisationResource leadOrganisation) {
        organisationRowViewModels.add(0, new ApplicationTeamManagementOrganisationRowViewModel(leadOrganisation.getId(),
                leadOrganisation.getName(), true, new ArrayList<>()));
        return organisationRowViewModels;
    }

    private List<ApplicationTeamManagementOrganisationRowViewModel> appendLeadApplicant(
                List<ApplicationTeamManagementOrganisationRowViewModel> organisationRowViewModels,
                ApplicationResource applicationResource) {

        organisationRowViewModels.stream().filter(ApplicationTeamManagementOrganisationRowViewModel::isLead).findFirst().ifPresent(
                applicationTeamOrganisationRowViewModel -> applicationTeamOrganisationRowViewModel.getApplicants().add(0,
                        getLeadApplicantViewModel(getLeadApplicant(applicationResource))));
        return organisationRowViewModels;
    }

    private ApplicationTeamManagementOrganisationRowViewModel getOrganisationViewModel(InviteOrganisationResource inviteOrganisationResource, long leadOrganisationId) {
        boolean lead = leadOrganisationId == inviteOrganisationResource.getOrganisation();
        return new ApplicationTeamManagementOrganisationRowViewModel(inviteOrganisationResource.getOrganisation(),
                getOrganisationName(inviteOrganisationResource), lead, inviteOrganisationResource.getInviteResources()
                .stream().map(this::getApplicantViewModel).collect(toList()));
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamManagementApplicantRowViewModel(getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending);
    }

    private ApplicationTeamManagementApplicantRowViewModel getLeadApplicantViewModel(UserResource userResource) {
        return new ApplicationTeamManagementApplicantRowViewModel(userResource.getName(), userResource.getEmail(), true, false);
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
