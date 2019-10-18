package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationUserRowViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private CompetitionRestService competitionRestService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        boolean isMonitoringOfficer = loggedInUser.getId().equals(project.getMonitoringOfficerUser());

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        OrganisationResource loggedInUserOrg;
        if(isMonitoringOfficer) {
            loggedInUserOrg = null;
        } else {
            ProjectUserResource loggedInProjectUser = simpleFindFirst(projectUsers,
                                                  pu -> pu.getUser().equals(loggedInUser.getId())).get();

            loggedInUserOrg = simpleFindFirst(projectOrganisations,
                                              org -> org.getId().equals(loggedInProjectUser.getOrganisation())).get();
        }

        List<ProjectUserInviteResource> invitedUsers = projectInviteRestService.getInvitesByProject(projectId).getSuccess();

        boolean isLead = leadOrganisation.equals(loggedInUserOrg);

        List<ProjectOrganisationViewModel> partnerOrgModels = projectOrganisations.stream()
                .map(org -> mapToProjectOrganisationViewModel(projectUsers,
                        invitedUsers,
                        org,
                        org.equals(leadOrganisation),
                        org.equals(loggedInUserOrg)))
                .sorted()
                .collect(toList());
        ProjectOrganisationViewModel loggedInUserOrgModel = getLoggedInUserOrgModel(partnerOrgModels, loggedInUserOrg, isMonitoringOfficer);

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
                isMonitoringOfficer,
                false);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private ProjectOrganisationViewModel mapToProjectOrganisationViewModel(List<ProjectUserResource> totalUsers, List<ProjectUserInviteResource> totalInvites, OrganisationResource organisation, boolean isLead, boolean editable) {
        List<ProjectUserResource> usersForOrganisation = simpleFilter(totalUsers,
                user -> user.getOrganisation().equals(organisation.getId()));
        List<ProjectUserInviteResource> invitesForOrganisation = simpleFilter(totalInvites,
                invite -> invite.getOrganisation().equals(organisation.getId()));
        return new ProjectOrganisationViewModel(mapUsersToViewModelRows(usersForOrganisation, invitesForOrganisation), organisation.getName(), organisation.getId(), isLead, editable);
    }

    private ProjectOrganisationViewModel getLoggedInUserOrgModel(List<ProjectOrganisationViewModel> partnerOrgModels,
                                                                 OrganisationResource loggedInUserOrg,
                                                                 boolean isMonitoringOfficer) {
        if(isMonitoringOfficer) {
            return null;
        }
        return partnerOrgModels.stream()
                .filter(org -> org.getOrgId() == loggedInUserOrg.getId())
                .findFirst()
                .orElse(null);
    }

    private List<ProjectOrganisationUserRowViewModel> mapUsersToViewModelRows(List<ProjectUserResource> usersForOrganisation, List<ProjectUserInviteResource> invitesForOrganistaion) {

        List<ProjectOrganisationUserRowViewModel> partnerUsers = usersForOrganisation.stream()
                .filter(pu -> !(pu.isProjectManager() || pu.isFinanceContact()))
                .map(pu -> new ProjectOrganisationUserRowViewModel(pu.getEmail(),
                        pu.getUserName(),
                        pu.getUser(),
                        false,
                        false,
                        false))
                .distinct()
                .collect(toList());

        partnerUsers.addAll(invitesForOrganistaion.stream()
                .filter(invite -> invite.getStatus() != InviteStatus.OPENED)
                .map(invite -> new ProjectOrganisationUserRowViewModel(invite.getEmail(),
                        invite.getName(),
                        invite.getId(),
                        false,
                        false,
                        true))
                .collect(toList()));

        Optional<ProjectUserResource> financeContact = simpleFindFirst(usersForOrganisation,
                ProjectUserResource::isFinanceContact);

        financeContact.ifPresent(fc ->
                simpleFindFirst(partnerUsers,
                        user -> user.getId() == fc.getUser()).get().setFinanceContact(true));

        Optional<ProjectUserResource> projectManager = simpleFindFirst(usersForOrganisation,
                                                                       ProjectUserResource::isProjectManager);

        projectManager.ifPresent(pm ->
                                         simpleFindFirst(partnerUsers,
                                                         user -> user.getId() == pm.getUser()).get().setProjectManager(true));

        return partnerUsers;
    }
}