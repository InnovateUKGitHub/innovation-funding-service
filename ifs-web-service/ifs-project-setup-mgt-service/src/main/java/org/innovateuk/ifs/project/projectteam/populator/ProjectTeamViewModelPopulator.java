package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationUserRowViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

@Component
public class ProjectTeamViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Value("${ifs.project.team.change.enabled:false}")
    private boolean pcrEnabled;

    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);


        List<ProjectUserInviteResource> invitedUsers = projectInviteRestService.getInvitesByProject(projectId).getSuccess();

        List<ProjectOrganisationViewModel> partnerOrgModels = projectOrganisations.stream()
                .map(org -> mapToProjectOrganisationViewModel(projectUsers,
                                                              invitedUsers,
                                                              org,
                                                              org.equals(leadOrganisation),
                                                              true))  // all organisations editable for internal users
                .sorted()
                .collect(toList());

        boolean projectIsNotActive = !projectResource.getProjectState().isActive();

        // support users and ifs admins can edit, other internal users have read only view only
        boolean isReadOnly = !loggedInUser.hasAnyRoles(IFS_ADMINISTRATOR, SUPPORT) || projectIsNotActive;

        return new ProjectTeamViewModel(
                projectResource.getCompetitionName(),
                projectResource.getCompetition(),
                projectResource.getName(),
                projectResource.getId(),
                partnerOrgModels,
                null,
                getProjectManager(projectResource.getId()).orElse(null),
                false,
                loggedInUser.getId(),
                false,
                true,
                isReadOnly,
                canInvitePartnerOrganisation(projectResource, loggedInUser));
    }

    private boolean canInvitePartnerOrganisation(ProjectResource project, UserResource user) {
        return pcrEnabled
                && user.hasRole(PROJECT_FINANCE)
                && !project.isSpendProfileGenerated()
                && project.getProjectState().isActive();
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

    private List<ProjectOrganisationUserRowViewModel> mapUsersToViewModelRows(List<ProjectUserResource> usersForOrganisation, List<ProjectUserInviteResource> invitesForOrganisation) {

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

        partnerUsers.addAll(invitesForOrganisation.stream()
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

        financeContact.ifPresent(fc -> partnerUsers.stream()
                .filter(user -> user.getId() == fc.getUser())
                .findFirst()
                .get()
                .setFinanceContact(true));

        Optional<ProjectUserResource> projectManager = simpleFindFirst(usersForOrganisation,
                                                                       ProjectUserResource::isProjectManager);

        projectManager.ifPresent(pm -> partnerUsers.stream()
                .filter(user -> user.getId() == pm.getUser())
                .findFirst()
                .get()
                .setProjectManager(true));

        return partnerUsers;
    }
}
