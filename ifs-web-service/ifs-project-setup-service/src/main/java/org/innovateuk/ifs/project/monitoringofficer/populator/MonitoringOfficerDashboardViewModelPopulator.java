package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.documents.populator.DocumentsPopulator;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardDocumentSectionViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.sections.SectionStatus.MO_ACTION_REQUIRED;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;
    private final MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;
    private final SetupSectionStatus setupSectionStatus;
    private final CompetitionRestService competitionRestService;
    private final ProjectFilterPopulator projectFilterPopulator;
    private final DocumentsPopulator documentsPopulator;

    public MonitoringOfficerDashboardViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService,
                                                        MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator,
                                                        SetupSectionStatus setupSectionStatus,
                                                        CompetitionRestService competitionRestService,
                                                        ProjectFilterPopulator projectFilterPopulator,
                                                        DocumentsPopulator documentsPopulator) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
        this.monitoringOfficerSummaryViewModelPopulator = monitoringOfficerSummaryViewModelPopulator;
        this.setupSectionStatus = setupSectionStatus;
        this.competitionRestService = competitionRestService;
        this.projectFilterPopulator = projectFilterPopulator;
        this.documentsPopulator = documentsPopulator;
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(projects);

        return new MonitoringOfficerDashboardViewModel(buildProjectDashboardRows(projects, user), monitoringOfficerSummaryViewModel);
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user,
                                                        boolean projectInSetup,
                                                        boolean previousProject,
                                                        boolean documentsComplete,
                                                        boolean documentsIncomplete,
                                                        boolean documentsAwaitingReview) {
        List<ProjectResource> projectsFilteredByState = monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(),
                projectInSetup, previousProject).getSuccess();
        List<ProjectResource> projectsFilteredByDocuments = projectsFilteredByDocuments(projectsFilteredByState, documentsComplete, documentsIncomplete, documentsAwaitingReview);
        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(user);

        return new MonitoringOfficerDashboardViewModel(buildProjectDashboardRows(projectsFilteredByDocuments, user), monitoringOfficerSummaryViewModel);
    }

    private String documentSectionStatusMOView(ProjectResource project, CompetitionResource competition) {
        return setupSectionStatus.documentsSectionStatus(false, project, competition, true).getStatus();
//
//        if (documentsPopulator.populateAllDocuments(project.getId(), user.getId()).getDocuments().stream().anyMatch(documentStatus -> DocumentStatus.SUBMITTED.equals(documentStatus.getStatus()))) {
//            return MO_ACTION_REQUIRED.getStatus();
//        } else if (documentsPopulator.populateAllDocuments(project.getId(), user.getId()).getDocuments().stream().allMatch(documentStatus -> DocumentStatus.APPROVED.equals(documentStatus.getStatus()))) {
//            return TICK.getStatus();
//        } else {
//            return INCOMPLETE.getStatus();
//        }
    }

    private List<ProjectDashboardRowViewModel> buildProjectDashboardRows(List<ProjectResource> projects, UserResource user) {
        List<ProjectResource> sortedProjects = sortProjects(projects);

        return sortedProjects.stream()
                .map(project ->
                        new ProjectDashboardRowViewModel(project,
                                new MonitoringOfficerDashboardDocumentSectionViewModel(documentSectionStatusMOView(project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess()),
                                        projectFilterPopulator.hasDocumentSection(project),
                                        project.getId(),
                                        hasDocumentAwaitingReview(project))))
                .collect(toList());
    }

    private List<ProjectResource> sortProjects(List<ProjectResource> projects) {
        return projects.stream()
                .sorted(Comparator.comparing(projectResource -> projectResource.getProjectState().getMoDisplayOrder()))
                .collect(toList());
    }

    private List<ProjectResource> projectsFilteredByDocuments(List<ProjectResource> projects, boolean documentsComplete, boolean documentsIncomplete, boolean documentsAwaitingReview) {

        List<ProjectResource> projectsWithDocumentsComplete = projectFilterPopulator.getProjectsWithDocumentsComplete(projects);
        List<ProjectResource> projectsWithDocumentsInComplete = projectFilterPopulator.getProjectsWithDocumentsInComplete(projects);
        List<ProjectResource> projectsWithDocumentsAwaitingReview = projectFilterPopulator.getProjectsWithDocumentsAwaitingReview(projects);

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

    private boolean hasDocumentAwaitingReview(ProjectResource project) {
        return projectFilterPopulator.hasDocumentSection(project) && setupSectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(MO_ACTION_REQUIRED);
    }
}
