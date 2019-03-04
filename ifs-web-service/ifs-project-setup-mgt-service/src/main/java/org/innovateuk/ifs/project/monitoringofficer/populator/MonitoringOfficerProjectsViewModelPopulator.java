package org.innovateuk.ifs.project.monitoringofficer.populator;


import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignedProjectViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerProjectsViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerUnassignedProjectViewModel;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class MonitoringOfficerProjectsViewModelPopulator {


    private ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService;

    public MonitoringOfficerProjectsViewModelPopulator(ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService) {
        this.projectMonitoringOfficerRestService = projectMonitoringOfficerRestService;
    }

    public MonitoringOfficerProjectsViewModel populate(long monitoringOfficerId) {
        ProjectMonitoringOfficerResource projectMonitoringOfficerResource =
                projectMonitoringOfficerRestService.getProjectMonitoringOfficer(monitoringOfficerId).getSuccess();

        return new MonitoringOfficerProjectsViewModel(
                projectMonitoringOfficerResource.getId(),
                projectMonitoringOfficerResource.getFullName(),
                1,
                singletonList(new MonitoringOfficerAssignedProjectViewModel(
                        119,
                        2,
                        5,
                        "Grade crossing manufacture and supply",
                        "Vitruvius, Stonework Limited"
                )),
                asList( new MonitoringOfficerUnassignedProjectViewModel(1, "foo"),
                        new MonitoringOfficerUnassignedProjectViewModel(2, "bar")
                )
        );
    }
}