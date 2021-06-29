package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.documents.populator.FilterDocumentsPopulator;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MonitoringOfficerSummaryViewModelPopulator {

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Autowired
    private FilterDocumentsPopulator filterDocumentsPopulator;

    public MonitoringOfficerSummaryViewModelPopulator() {
    }

    public MonitoringOfficerSummaryViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        return new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsComplete(projects), getDocumentsInComplete(projects), getDocumentsAwaitingReview(projects));
    }

    public MonitoringOfficerSummaryViewModel populate(List<ProjectResource> projects) {
        return new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsComplete(projects), getDocumentsInComplete(projects), getDocumentsAwaitingReview(projects));
    }

    public int getInSetupProjectCount(List<ProjectResource> projects) {
        List<ProjectResource> inSetupProjects = projects.stream()
                .filter(project -> !project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return inSetupProjects.size();
    }

    public int getPreviousProjectCount(List<ProjectResource> projects) {
        List<ProjectResource> previousProjects = projects.stream()
                .filter(project -> project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return previousProjects.size();
    }

    public int getDocumentsComplete(List<ProjectResource> projects) {
        return filterDocumentsPopulator.getProjectsWithDocumentsComplete(projects).size();
    }

    public int getDocumentsInComplete(List<ProjectResource> projects) {
        return filterDocumentsPopulator.getProjectsWithDocumentsInComplete(projects).size();
    }

    public int getDocumentsAwaitingReview(List<ProjectResource> projects) {
        return filterDocumentsPopulator.getProjectsWithDocumentsAwaitingReview(projects).size();
    }
}
