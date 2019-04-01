package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MonitoringOfficerDashboardViewModelPopulator {

    private final ProjectMonitoringOfficerRestService monitoringOfficerRestService;
    private final CompetitionRestService competitionRestService;

    public MonitoringOfficerDashboardViewModelPopulator(ProjectMonitoringOfficerRestService monitoringOfficerRestService, CompetitionRestService competitionRestService) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
        this.competitionRestService = competitionRestService;
    }

    public MonitoringOfficerDashboardViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        List<ProjectDashboardRowViewModel> projectViews = projects.stream().map(project -> {
            CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
            return new ProjectDashboardRowViewModel(project.getApplication(), competition.getName(), project.getId(), project.getName());
        }).collect(toList());
        return new MonitoringOfficerDashboardViewModel(projectViews);
    }
}
