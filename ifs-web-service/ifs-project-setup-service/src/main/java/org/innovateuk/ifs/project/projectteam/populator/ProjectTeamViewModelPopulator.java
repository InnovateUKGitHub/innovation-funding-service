package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.projectteam.viewmodel.*;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

@Component
public class ProjectTeamViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        boolean isMonitoringOfficer = monitoringOfficerRestService.isMonitoringOfficerOnProject(projectId, loggedInUser.getId()).getSuccess();

        List<ProjectUserResource> projectUsers = projectService.getDisplayProjectUsersForProject(project.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        OrganisationResource loggedInUserOrg;
        if(isMonitoringOfficer) {
            loggedInUserOrg = null;
        } else {
            loggedInUserOrg = projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess();
        }

        List<ProjectUserInviteResource> invitedUsers = projectInviteRestService.getInvitesByProject(projectId).getSuccess();

        boolean isLead = leadOrganisation.equals(loggedInUserOrg);

        List<ProjectTeamOrganisationViewModel> partnerOrgModels = projectOrganisations.stream()
                .map(org -> mapToProjectOrganisationViewModel(projectId,
                        projectUsers,
                        invitedUsers,
                        org,
                        loggedInUser,
                        org.equals(leadOrganisation),
                        org.equals(loggedInUserOrg)))
                .sorted()
                .collect(toList());
        ProjectTeamOrganisationViewModel loggedInUserOrgModel = getLoggedInUserOrgModel(partnerOrgModels, loggedInUserOrg, isMonitoringOfficer);

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);

        return new ProjectTeamViewModel(
                project,
                partnerOrgModels,
                loggedInUserOrgModel,
                getProjectManager(project.getId()).orElse(null),
                isLead,
                loggedInUser.getId(),
                statusAccessor.isGrantOfferLetterGenerated(),
                false,
                project.getProjectState().isLive(),
                false,
                false);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private ProjectTeamOrganisationViewModel mapToProjectOrganisationViewModel(long projectId, List<ProjectUserResource> totalUsers, List<ProjectUserInviteResource> totalInvites, OrganisationResource organisation, UserResource loggedInUser, boolean isLead, boolean loggedInUsersOrganisation) {
        List<ProjectUserResource> usersForOrganisation = simpleFilter(totalUsers,
                user -> user.getOrganisation().equals(organisation.getId()));
        List<ProjectUserInviteResource> invitesForOrganisation = simpleFilter(totalInvites,
                invite -> invite.getOrganisation().equals(organisation.getId()));

        AddressResource address = null;
        if (organisation.isInternational()) {
            address = partnerOrganisationRestService.getPartnerOrganisation(projectId, organisation.getId()).getSuccess().getInternationalAddress();
        }
        return new ProjectTeamOrganisationViewModel(mapUsersToViewModelRows(usersForOrganisation, invitesForOrganisation, loggedInUser, loggedInUsersOrganisation), organisation.getName(), organisation.getId(), isLead, loggedInUsersOrganisation, null, address);
    }

    private ProjectTeamOrganisationViewModel getLoggedInUserOrgModel(List<ProjectTeamOrganisationViewModel> partnerOrgModels,
                                                                     OrganisationResource loggedInUserOrg,
                                                                     boolean isMonitoringOfficer) {
        if(isMonitoringOfficer) {
            return null;
        }
        return partnerOrgModels.stream()
                .filter(org -> org.getId() == loggedInUserOrg.getId())
                .findFirst()
                .orElse(null);
    }

    private List<AbstractProjectTeamRowViewModel> mapUsersToViewModelRows(List<ProjectUserResource> users, List<ProjectUserInviteResource> invites, UserResource loggedInUser, boolean loggedInUsersOrganisation) {

        List<ProjectTeamUserViewModel> partnerUsers = users.stream()
                .filter(pu -> !(pu.isProjectManager() || pu.isFinanceContact()))
                .map(pu -> new ProjectTeamUserViewModel(
                        pu.getUser(),
                        pu.getEmail(),
                        pu.getUserName(),
                        !loggedInUser.getId().equals(pu.getUser()) && loggedInUsersOrganisation))
                .distinct()
                .collect(toList());

        Optional<ProjectUserResource> financeContact = simpleFindFirst(users,
                ProjectUserResource::isFinanceContact);

        financeContact.ifPresent(fc ->
                simpleFindFirst(partnerUsers,
                        user -> user.getId() == fc.getUser()).get().setFinanceContact(true));

        Optional<ProjectUserResource> projectManager = simpleFindFirst(users,
                                                                       ProjectUserResource::isProjectManager);

        projectManager.ifPresent(pm ->
                                         simpleFindFirst(partnerUsers,
                                                         user -> user.getId() == pm.getUser()).get().setProjectManager(true));

        List<ProjectTeamInviteViewModel> inviteViews = invites.stream()
                .filter(invite -> invite.getStatus() != InviteStatus.OPENED)
                .map(invite -> new ProjectTeamInviteViewModel(
                        invite.getId(),
                        invite.getEmail(),
                        invite.getName(),
                        invite.getSentOn(),
                        loggedInUsersOrganisation,
                        loggedInUsersOrganisation))
                .collect(toList());

        List<AbstractProjectTeamRowViewModel> rows = new ArrayList<>();
        rows.addAll(partnerUsers);
        rows.addAll(inviteViews);
        return rows;
    }
}