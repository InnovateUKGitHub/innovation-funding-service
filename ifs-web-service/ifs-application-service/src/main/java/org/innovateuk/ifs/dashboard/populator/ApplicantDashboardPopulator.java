package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionService competitionService;


    public ApplicantDashboardViewModel populate(UserResource user) {
        Map<Long, Integer> applicationProgress = applicationService.getProgress(user.getId());

        List<ApplicationResource> inProgress = applicationService.getInProgress(user.getId());
        List<ApplicationResource> finished = applicationService.getFinished(user.getId());

        List<ProjectResource> projectsInSetup = projectService.findByUser(user.getId()).getSuccessObject();

        List<ApplicationResource> applicationsForProjectsInSetup = getApplicationsForProjectsInSetup(projectsInSetup);

        Map<Long, CompetitionResource> competitions = createCompetitionMap(inProgress, finished, applicationsForProjectsInSetup);
        Map<Long, ApplicationState> applicationStatusMap = createApplicationStateMap(inProgress, finished);


        Map<Long, ProcessRoleResource> inProgressProcessRoles = simpleToMap(inProgress, ApplicationResource::getId,
                applicationResource ->  processRoleService.findProcessRole(user.getId(), applicationResource.getId()));

        List<Long> applicationsAssigned = getAssignedApplications(inProgressProcessRoles, user);
        List<Long> leadApplicantApplications = getLeadApplicantApplications(inProgressProcessRoles, user);

        return new ApplicantDashboardViewModel(applicationProgress, inProgress,
                applicationsAssigned, finished,
                projectsInSetup, competitions, applicationStatusMap, leadApplicantApplications);
    }

    private List<Long> getAssignedApplications(Map<Long, ProcessRoleResource> inProgressProcessRoles, UserResource user) {
        return inProgressProcessRoles.entrySet().stream().filter(entry -> {
            if(!UserRoleType.LEADAPPLICANT.getName().equals(entry.getValue().getRoleName())) {
                int count = applicationService.getAssignedQuestionsCount(entry.getKey(), entry.getValue().getId());
                return count != 0;
            } else {
                return false;
            }
        }).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private List<Long> getLeadApplicantApplications(Map<Long, ProcessRoleResource> inProgressProcessRoles, UserResource user){
        return inProgressProcessRoles.entrySet().stream()
                .filter(entry -> UserRoleType.LEADAPPLICANT.getName().equals(entry.getValue().getRoleName()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @SafeVarargs
    private final Map<Long, ApplicationState> createApplicationStateMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> application.getApplicationState()
                )
            );
    }

    @SafeVarargs
    private final Map<Long, CompetitionResource> createCompetitionMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> competitionService.getById(application.getCompetition()), (p1, p2) -> p1)
            );
    }

    private final List<ApplicationResource> getApplicationsForProjectsInSetup(List<ProjectResource> resources){
        return resources.stream().map(project -> applicationService.getById(project.getApplication())).collect(Collectors.toList());
    }
}
