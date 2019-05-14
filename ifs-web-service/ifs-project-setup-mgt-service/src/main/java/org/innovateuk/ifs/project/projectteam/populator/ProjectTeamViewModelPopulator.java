package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationUserRowViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

@Component
public class ProjectTeamViewModelPopulator {

    private final ProjectService projectService;

    private final CompetitionRestService competitionRestService;

    private final StatusService statusService;

    private final ProjectDetailsService projectDetailsService;

    public ProjectTeamViewModelPopulator(ProjectService projectService,
                                         CompetitionRestService competitionRestService,
                                         StatusService statusService,
                                         ProjectDetailsService projectDetailsService) {
        this.projectService = projectService;
        this.competitionRestService = competitionRestService;
        this.statusService = statusService;
        this.projectDetailsService = projectDetailsService;
    }

    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {


        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);


        List<ProjectUserInviteResource> invitedUsers = projectDetailsService.getInvitesByProject(projectId).getSuccess();

        List<ProjectOrganisationViewModel> partnerOrgModels = projectOrganisations.stream()
                .map(org -> mapToProjectOrganisationViewModel(projectUsers,
                                                              invitedUsers,
                                                              org,
                                                              org.equals(leadOrganisation),
                                                              true))
                .sorted()
                .collect(toList());

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());

        return new ProjectTeamViewModel(
                competitionResource.getName(),
                projectResource.getName(),
                projectResource.getId(),
                partnerOrgModels,
                null,
                getProjectManager(projectResource.getId()).orElse(null),
                false,
                loggedInUser.getId(),
                false,
                true);
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
