package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;

    public MonitoringOfficerDashboardViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        List<ProjectDashboardRowViewModel> projectViews = projects.stream().map(project ->
                new ProjectDashboardRowViewModel(project)).collect(toList());
        return new MonitoringOfficerDashboardViewModel(projectViews);
    }
}
