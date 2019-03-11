package org.innovateuk.ifs.project.monitoringofficer.populator;


import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignedProjectViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerProjectsViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerUnassignedProjectViewModel;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

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