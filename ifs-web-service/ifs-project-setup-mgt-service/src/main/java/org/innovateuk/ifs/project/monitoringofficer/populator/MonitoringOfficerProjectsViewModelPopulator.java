package org.innovateuk.ifs.project.monitoringofficer.populator;


import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignedProjectViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerProjectsViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerUnassignedProjectViewModel;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class MonitoringOfficerProjectsViewModelPopulator {

    private MonitoringOfficerRestService projectMonitoringOfficerRestService;

    public MonitoringOfficerProjectsViewModelPopulator(MonitoringOfficerRestService projectMonitoringOfficerRestService) {
        this.projectMonitoringOfficerRestService = projectMonitoringOfficerRestService;
    }

    public MonitoringOfficerProjectsViewModel populate(long monitoringOfficerId) {
        MonitoringOfficerResource projectMonitoringOfficerResource =
                projectMonitoringOfficerRestService.getProjectMonitoringOfficer(monitoringOfficerId).getSuccess();

        return new MonitoringOfficerProjectsViewModel(
                projectMonitoringOfficerResource.getUserId(),
                projectMonitoringOfficerResource.getFullName(),
                projectMonitoringOfficerResource.getAssignedProjects().size(),
                simpleMap(projectMonitoringOfficerResource.getAssignedProjects(), MonitoringOfficerProjectsViewModelPopulator::map),
                simpleMap(projectMonitoringOfficerResource.getUnassignedProjects(), MonitoringOfficerProjectsViewModelPopulator::map)
        );
    }

    private static MonitoringOfficerAssignedProjectViewModel map(MonitoringOfficerAssignedProjectResource project) {
        return new MonitoringOfficerAssignedProjectViewModel(
                project.getProjectId(),
                project.getApplicationId(),
                project.getCompetitionId(),
                project.getProjectName(),
                project.getLeadOrganisationName()
        );
    }

    private static MonitoringOfficerUnassignedProjectViewModel map(MonitoringOfficerUnassignedProjectResource project) {
        return new MonitoringOfficerUnassignedProjectViewModel(
                project.getProjectId(),
                project.getApplicationId(),
                project.getProjectName()
        );
    }
}