package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private SetupStatusViewModelPopulator setupStatusViewModelPopulator;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationService;


    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        ProjectUserResource loggedInProjectUser = simpleFindFirst(projectUsers,
                                                                  u -> u.getId().equals(loggedInUser.getId())).get();
        OrganisationResource loggedInUserOrg = simpleFindFirst(projectOrganisations,
                                                               org -> org.getId().equals(loggedInProjectUser.getOrganisation())).get();

        boolean isLead = loggedInUserOrg.equals(leadOrganisation);

        ProjectOrganisationViewModel leadOrgModel = mapToProjectOrganisationViewModel(projectUsers, leadOrganisation, true);
        ProjectOrganisationViewModel loggedInUserOrgModel = mapToProjectOrganisationViewModel(projectUsers, loggedInUserOrg, isLead);
        List<ProjectOrganisationViewModel> partnerOrgModels = projectOrganisations
                .stream()
                .filter(org -> !org.getId().equals(leadOrganisation.getId()))
                .map(org -> mapToProjectOrganisationViewModel(projectUsers, org, false))
                .collect(Collectors.toList());

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);
        boolean spendProfileGenerated = statusAccessor.isSpendProfileGenerated();
        boolean monitoringOfficerAssigned = statusAccessor.isMonitoringOfficerAssigned();

        return new ProjectTeamViewModel(
                competitionResource.getName(),
                projectResource.getName(),
                projectResource.getId(),
                partnerOrgModels,
                loggedInUserOrgModel,
                leadOrgModel,
                getProjectManager(projectResource.getId()).orElse(null),
                isLead,
                monitoringOfficerAssigned,
                spendProfileGenerated,
                statusAccessor.isGrantOfferLetterGenerated(),
                false);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private ProjectOrganisationViewModel mapToProjectOrganisationViewModel(List<ProjectUserResource> totalUsers, OrganisationResource organisation, boolean isLead) {
        List<ProjectUserResource> usersForOrganisation = simpleFilter(totalUsers,
                                                                      user -> user.getOrganisation().equals(organisation.getId()));
        return new ProjectOrganisationViewModel(usersForOrganisation, organisation.getName(), isLead);
    }


}
