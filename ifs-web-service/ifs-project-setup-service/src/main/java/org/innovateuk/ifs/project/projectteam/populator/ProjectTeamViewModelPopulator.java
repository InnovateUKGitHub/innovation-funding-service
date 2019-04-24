package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
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
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Component
public class ProjectTeamViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

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
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();
        boolean partnerProjectLocationRequired = competitionResource.isLocationPerPartner();

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> organisations
                = new PrioritySorting<>(getPartnerOrganisations(projectUsers), leadOrganisation, OrganisationResource::getName).unwrap();

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);
        boolean spendProfileGenerated = statusAccessor.isSpendProfileGenerated();
        boolean monitoringOfficerAssigned = statusAccessor.isMonitoringOfficerAssigned();

        boolean allProjectDetailsFinanceContactsAndProjectLocationsAssigned = setupStatusViewModelPopulator.checkLeadPartnerProjectDetailsProcessCompleted(teamStatus, partnerProjectLocationRequired);

        return new ProjectTeamViewModel(projectResource, loggedInUser,
                                        getUsersPartnerOrganisations(loggedInUser, projectUsers),
                                        organisations,
                                        partnerProjectLocationRequired ?
                                                partnerOrganisationService.getProjectPartnerOrganisations(projectId).getSuccess()
                                                : Collections.emptyList(),
                                        leadOrganisation, applicationResource, projectUsers, competitionResource,
                                        projectService.isUserLeadPartner(projectId, loggedInUser.getId()), allProjectDetailsFinanceContactsAndProjectLocationsAssigned,
                                        getProjectManager(projectResource.getId()).orElse(null), monitoringOfficerAssigned, spendProfileGenerated, statusAccessor.isGrantOfferLetterGenerated(), false);
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRole() == PARTNER.getId())
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccess())
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }

    private List<Long> getUsersPartnerOrganisations(UserResource loggedInUser, List<ProjectUserResource> projectUsers) {
        List<ProjectUserResource> partnerProjectUsers = simpleFilter(projectUsers,
                                                                     user -> loggedInUser.getId().equals(user.getUser()) && user.getRoleName().equals(PARTNER.getName()));
        return simpleMap(partnerProjectUsers, ProjectUserResource::getOrganisation);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }


}
