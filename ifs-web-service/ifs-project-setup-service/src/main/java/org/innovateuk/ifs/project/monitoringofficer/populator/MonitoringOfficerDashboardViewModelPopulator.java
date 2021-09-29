package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.*;
import org.innovateuk.ifs.project.navigation.Pagination;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.sections.SectionStatus.INCOMPLETE;
import static org.innovateuk.ifs.sections.SectionStatus.MO_ACTION_REQUIRED;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;
    private final MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;
    private final SetupSectionStatus setupSectionStatus;
    private final CompetitionRestService competitionRestService;
    private final ProjectFilterPopulator projectFilterPopulator;
    private final ProjectService projectService;

    @Value("${ifs.monitoringofficer.journey.update.enabled}")
    private boolean isMOJourneyUpdateEnabled;

    @Value("${ifs.monitoringofficer.spendprofile.update.enabled}")
    private boolean isMOSpendProfileUpdateEnabled;

    @Value("${ifs.monitoringofficer.dashboard.filter.enabled}")
    private boolean moDashboardFilterEnabled;

    public MonitoringOfficerDashboardViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService,
                                                        MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator,
                                                        SetupSectionStatus setupSectionStatus,
                                                        CompetitionRestService competitionRestService,
                                                        ProjectFilterPopulator projectFilterPopulator,
                                                        ProjectService projectService) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
        this.monitoringOfficerSummaryViewModelPopulator = monitoringOfficerSummaryViewModelPopulator;
        this.setupSectionStatus = setupSectionStatus;
        this.competitionRestService = competitionRestService;
        this.projectFilterPopulator = projectFilterPopulator;
        this.projectService = projectService;
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(projects);

        return new MonitoringOfficerDashboardViewModel(buildProjectDashboardRows(projects, user), monitoringOfficerSummaryViewModel,
                isMOJourneyUpdateEnabled, isMOSpendProfileUpdateEnabled, moDashboardFilterEnabled, null);
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user,
                                                        String keywordSearch,
                                                        boolean projectInSetup,
                                                        boolean previousProject,
                                                        boolean documentsComplete,
                                                        boolean documentsIncomplete,
                                                        boolean documentsAwaitingReview,
                                                        boolean spendProfileComplete,
                                                        boolean spendProfileIncomplete,
                                                        boolean spendProfileAwaitingReview,
                                                        int pageNumber, int pageSize) {

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(),
                pageNumber, pageSize, keywordSearch, projectInSetup, previousProject).getSuccess();

        List<ProjectResource> projectsFilteredByState = monitoringOfficerDashboardPageResource.getContent();

        if (moDashboardFilterEnabled) {
            if (documentsComplete || documentsIncomplete || documentsAwaitingReview) {
                projectsFilteredByState = projectsFilteredByDocuments(projectsFilteredByState
                        , documentsComplete
                        , documentsIncomplete
                        , documentsAwaitingReview);
            }

            if (spendProfileComplete || spendProfileIncomplete || spendProfileAwaitingReview) {
                projectsFilteredByState = projectsFilteredBySpendProfile(projectsFilteredByState
                        , spendProfileComplete
                        , spendProfileIncomplete
                        , spendProfileAwaitingReview);
            }
        }

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(user);
        MonitoringOfficerDashboardViewModel monitoringOfficerDashboardViewModel = new MonitoringOfficerDashboardViewModel(
                buildProjectDashboardRows(projectsFilteredByState, user), monitoringOfficerSummaryViewModel, isMOJourneyUpdateEnabled,
                isMOSpendProfileUpdateEnabled, moDashboardFilterEnabled, new Pagination(monitoringOfficerDashboardPageResource));

        return monitoringOfficerDashboardViewModel;
    }

    private String documentSectionStatusMOView(ProjectResource project, CompetitionResource competition) {
        return setupSectionStatus.documentsSectionStatus(false, project, competition, true).getStatus();
    }

    private String spendProfileStatusMOView(ProjectResource project) {
        if (!project.isSpendProfileGenerated()) {
            return INCOMPLETE.getStatus();
        }
        return projectFilterPopulator.getSpendProfileSectionStatus(project).getStatus();
    }

    private List<ProjectDashboardRowViewModel> buildProjectDashboardRows(List<ProjectResource> projects, UserResource user) {
         if (moDashboardFilterEnabled) {
            return projects.stream()
                    .map(project -> new ProjectDashboardRowViewModel(project,
                                            getMonitoringDashboardSectionsViewModel(project)))
                    .collect(toList());
        }
        return projects.stream()
                .map(ProjectDashboardRowViewModel::new)
                .collect(toList());
    }

    private MonitoringDashboardSectionsViewModel getMonitoringDashboardSectionsViewModel(ProjectResource project) {

        return new MonitoringDashboardSectionsViewModel(getMonitoringOfficerDashboardDocumentSectionViewModel(project),
                getMonitoringOfficerDashboardSpendProfileSectionViewModel(project));
    }


    private MonitoringOfficerDashboardDocumentSectionViewModel getMonitoringOfficerDashboardDocumentSectionViewModel(ProjectResource project) {
        return new MonitoringOfficerDashboardDocumentSectionViewModel(documentSectionStatusMOView(project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess()),
                projectFilterPopulator.hasDocumentSection(project),
                project.getId(),
                hasDocumentAwaitingReview(project));
    }

    private MonitoringOfficerDashboardSpendProfileSectionViewModel getMonitoringOfficerDashboardSpendProfileSectionViewModel(ProjectResource project) {
        return new MonitoringOfficerDashboardSpendProfileSectionViewModel(spendProfileStatusMOView(project),
                projectFilterPopulator.hasSpendProfileSection(project),
                project.getId(),
                spendProfileStatusMOView(project).equals(MO_ACTION_REQUIRED.getStatus()),
                getLeadPartnerOrganisationId(project.getId()));
    }

    private long getLeadPartnerOrganisationId(long projectId) {
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        return leadOrganisation.getId();
    }

    private List<ProjectResource> projectsFilteredByDocuments(List<ProjectResource> projects
            , boolean documentsComplete
            , boolean documentsIncomplete
            , boolean documentsAwaitingReview) {

        List<ProjectResource> projectsWithDocSection =  projects.stream()
                .filter(project -> projectFilterPopulator.hasDocumentSection(project))
                .collect(Collectors.toList());

        List<ProjectResource> projectsWithDocumentsComplete = projectFilterPopulator.getProjectsWithDocumentsComplete(projectsWithDocSection);
        List<ProjectResource> projectsWithDocumentsInComplete = projectFilterPopulator.getProjectsWithDocumentsInComplete(projectsWithDocSection);
        List<ProjectResource> projectsWithDocumentsAwaitingReview = projectFilterPopulator.getProjectsWithDocumentsAwaitingReview(projectsWithDocSection);

        if (documentsComplete && documentsIncomplete && documentsAwaitingReview) {
            return Stream.of(projectsWithDocumentsComplete, projectsWithDocumentsInComplete, projectsWithDocumentsAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (documentsComplete && documentsIncomplete) {
            return Stream.of(projectsWithDocumentsComplete, projectsWithDocumentsInComplete).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (documentsAwaitingReview && documentsComplete) {
            return Stream.of(projectsWithDocumentsComplete, projectsWithDocumentsAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (documentsIncomplete && documentsAwaitingReview) {
            return Stream.of(projectsWithDocumentsInComplete, projectsWithDocumentsAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (documentsComplete) {
            return projectsWithDocumentsComplete;
        } else if (documentsIncomplete) {
            return projectsWithDocumentsInComplete;
        } else if (documentsAwaitingReview) {
            return projectsWithDocumentsAwaitingReview;
        } else return projects;
    }

    private List<ProjectResource> projectsFilteredBySpendProfile(List<ProjectResource> projects
            , boolean spendProfileComplete
            , boolean spendProfileInComplete
            , boolean spendProfileAwaitingReview) {

        List<ProjectResource> projectsWithSpendProfileSection =  projects.stream()
                .filter(project -> projectFilterPopulator.hasSpendProfileSection(project))
                .collect(Collectors.toList());

        List<ProjectResource> projectsWithSpendProfileComplete = projectFilterPopulator.getProjectsWithSpendProfileComplete(projectsWithSpendProfileSection);
        List<ProjectResource> projectsWithSpendProfileInComplete = projectFilterPopulator.getProjectsWithSpendProfileInComplete(projectsWithSpendProfileSection);
        List<ProjectResource> projectsWithSpendProfileAwaitingReview = projectFilterPopulator.getProjectsWithSpendProfileAwaitingReview(projectsWithSpendProfileSection);

        if (spendProfileComplete && spendProfileInComplete && spendProfileAwaitingReview) {
            return Stream.of(projectsWithSpendProfileComplete, projectsWithSpendProfileInComplete, projectsWithSpendProfileAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (spendProfileComplete && spendProfileInComplete) {
            return Stream.of(projectsWithSpendProfileComplete, projectsWithSpendProfileInComplete).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (spendProfileAwaitingReview && spendProfileComplete) {
            return Stream.of(projectsWithSpendProfileComplete, projectsWithSpendProfileAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (spendProfileInComplete && spendProfileAwaitingReview) {
            return Stream.of(projectsWithSpendProfileInComplete, projectsWithSpendProfileAwaitingReview).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else if (spendProfileComplete) {
            return projectsWithSpendProfileComplete;
        } else if (spendProfileInComplete) {
            return projectsWithSpendProfileInComplete;
        } else if (spendProfileAwaitingReview) {
            return projectsWithSpendProfileAwaitingReview;
        } else return projects;

    }

    private boolean hasDocumentAwaitingReview(ProjectResource project) {
        return projectFilterPopulator.hasDocumentSection(project) && setupSectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(MO_ACTION_REQUIRED);
    }
}
