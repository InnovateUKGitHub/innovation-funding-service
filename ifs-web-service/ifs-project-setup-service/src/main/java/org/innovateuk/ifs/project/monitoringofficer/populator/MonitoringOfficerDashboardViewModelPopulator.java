package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardDocumentSectionViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;
    private final MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;
    private final SetupSectionStatus sectionStatus;
    private final CompetitionRestService competitionRestService;

    public MonitoringOfficerDashboardViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService,
                                                        MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator,
                                                        SetupSectionStatus sectionStatus,
                                                        CompetitionRestService competitionRestService) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
        this.monitoringOfficerSummaryViewModelPopulator = monitoringOfficerSummaryViewModelPopulator;
        this.sectionStatus = sectionStatus;
        this.competitionRestService = competitionRestService;
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(projects);

        return new MonitoringOfficerDashboardViewModel(buildProjectDashboardRows(projects), monitoringOfficerSummaryViewModel);
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user, boolean projectInSetup, boolean previousProject) {
        List<ProjectResource> projects = monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(),
                projectInSetup, previousProject).getSuccess();
        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = monitoringOfficerSummaryViewModelPopulator.populate(user);

        return new MonitoringOfficerDashboardViewModel(buildProjectDashboardRows(projects), monitoringOfficerSummaryViewModel);
    }

    private String documentSectionStatus(ProjectResource project, CompetitionResource competition) {
        return sectionStatus.documentsSectionStatus(false, project, competition).getStatus();
    }

    private boolean hasDocumentSection(CompetitionResource competition) {
        List<CompetitionDocumentResource> competitionDocuments = competition.getCompetitionDocuments();
        return competitionDocuments.size() != 0;
    }

    private List<ProjectDashboardRowViewModel> buildProjectDashboardRows(List<ProjectResource> projects) {
        List<ProjectResource> sortedProjects = sortProjects(projects);

        return sortedProjects.stream()
                .map(project ->
                    new ProjectDashboardRowViewModel(project, new MonitoringOfficerDashboardDocumentSectionViewModel(documentSectionStatus(project,
                            competitionRestService.getCompetitionById(project.getCompetition()).getSuccess()),
                            hasDocumentSection(competitionRestService.getCompetitionById(project.getCompetition()).getSuccess()),
                            project.getId())))
                .collect(toList());
    }

    private List<ProjectResource> sortProjects(List<ProjectResource> projects) {
        return projects.stream()
                .sorted(Comparator.comparing(projectResource -> projectResource.getProjectState().getMoDisplayOrder()))
                .collect(toList());
    }
}
