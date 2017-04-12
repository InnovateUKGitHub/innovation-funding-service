package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
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

import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

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
        Map<Long, ApplicationStatus> applicationStatusMap = createApplicationStatusMap(inProgress, finished);

        List<Long> applicationsAssigned = getAssignedApplications(inProgress, user);

        return new ApplicantDashboardViewModel(applicationProgress, inProgress,
                applicationsAssigned, finished,
                projectsInSetup, competitions, applicationStatusMap);
    }

    private List<Long> getAssignedApplications(List<ApplicationResource> inProgress, UserResource user){
        return inProgress.stream().filter(applicationResource -> {
                    ProcessRoleResource role = processRoleService.findProcessRole(user.getId(), applicationResource.getId());
                    if(!UserRoleType.LEADAPPLICANT.getName().equals(role.getRoleName())) {
                        int count = applicationService.getAssignedQuestionsCount(applicationResource.getId(), role.getId());
                        return count != 0;
                    }else{
                        return false;
                    }
                }
        ).mapToLong(ApplicationResource::getId).boxed().collect(Collectors.toList());
    }

    // TODO DW - INFUND-1555 - handle rest result
    @SafeVarargs
    private final Map<Long, ApplicationStatus> createApplicationStatusMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> application.getApplicationStatus()
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
