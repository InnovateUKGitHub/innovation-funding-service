package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitoringOfficerSummaryViewModelPopulator {

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Autowired
    private ProjectFilterPopulator projectFilterPopulator;

    @Value("${ifs.monitoringofficer.journey.update.enabled}")
    private boolean isMOJourneyUpdateEnabled;

    public MonitoringOfficerSummaryViewModelPopulator() {
    }

    public MonitoringOfficerSummaryViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        return isMOJourneyUpdateEnabled ?
                new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsCompleteCount(projects), getDocumentsInCompleteCount(projects), getDocumentsAwaitingReviewCount(projects)) :
                new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects));
    }

    public MonitoringOfficerSummaryViewModel populate(List<ProjectResource> projects) {
        return isMOJourneyUpdateEnabled ? new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsCompleteCount(projects), getDocumentsInCompleteCount(projects), getDocumentsAwaitingReviewCount(projects)) :
                new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects));
    }

    public int getInSetupProjectCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getInSetupProjects(projects).size();
    }

    public int getPreviousProjectCount(List<ProjectResource> projects) {
        return  projectFilterPopulator.getPreviousProject(projects).size();
    }

    public int getDocumentsCompleteCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithDocumentsComplete(projects).size();
    }

    public int getDocumentsInCompleteCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithDocumentsInComplete(projects).size();
    }

    public int getDocumentsAwaitingReviewCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithDocumentsAwaitingReview(projects).size();
    }

    public int getSpendProfileCompleteCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithSpendProfileComplete(projects).size();
    }

    public int getSpendProfileInCompleteCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithSpendProfileInComplete(projects).size();
    }

    public int getSpendProfileAwaitingReviewCount(List<ProjectResource> projects) {
        return projectFilterPopulator.getProjectsWithSpendProfileAwaitingReview(projects).size();
    }
}
