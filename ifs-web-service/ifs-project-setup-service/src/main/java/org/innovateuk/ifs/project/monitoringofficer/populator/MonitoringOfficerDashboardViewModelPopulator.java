package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;
    private final MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;

    public MonitoringOfficerDashboardViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService,
                                                        MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
        this.monitoringOfficerSummaryViewModelPopulator = monitoringOfficerSummaryViewModelPopulator;
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

    private List<ProjectDashboardRowViewModel> buildProjectDashboardRows(List<ProjectResource> projects) {
        return projects.stream()
                .map(project -> new ProjectDashboardRowViewModel(project))
                .collect(toList());
    }
}
