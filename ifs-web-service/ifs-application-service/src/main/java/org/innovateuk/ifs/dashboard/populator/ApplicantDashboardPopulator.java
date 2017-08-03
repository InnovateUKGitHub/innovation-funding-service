package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    private EnumSet<ApplicationState> inProgress = EnumSet.of(ApplicationState.CREATED, ApplicationState.OPEN);
    private EnumSet<ApplicationState> submitted = EnumSet.of(ApplicationState.SUBMITTED, ApplicationState.INELIGIBLE);
    private EnumSet<ApplicationState> finished = EnumSet.of(ApplicationState.APPROVED, ApplicationState.REJECTED, ApplicationState.INELIGIBLE_INFORMED);
    private EnumSet<CompetitionStatus> fundingNotYetCompete = EnumSet.of(CompetitionStatus.OPEN, CompetitionStatus.IN_ASSESSMENT, CompetitionStatus.FUNDERS_PANEL);
    private EnumSet<CompetitionStatus> fundingComplete = EnumSet.of(CompetitionStatus.ASSESSOR_FEEDBACK, CompetitionStatus.PROJECT_SETUP);
    private EnumSet<CompetitionStatus> open = EnumSet.of(CompetitionStatus.OPEN);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionsRestService competitionsRestService;


    public ApplicantDashboardViewModel populate(UserResource user) {
        List<ApplicationResource> allApplications = applicationRestService
                .getApplicationsByUserId(user.getId())
                .getSuccessObjectOrThrowException();

        Map<Long, ApplicationState> applicationStatusMap = createApplicationStateMap(allApplications);

        List<ApplicationResource> inProgress = simpleFilter(allApplications, this::applicationInProgress);

        Map<Long, Integer> applicationProgress =
                simpleToMap(inProgress, ApplicationResource::getId, a -> a.getCompletion().intValue());

        List<ApplicationResource> finished = simpleFilter(allApplications, this::applicationFinished);

        List<ProjectResource> projectsInSetup = projectService.findByUser(user.getId()).getSuccessObjectOrThrowException();

        List<ProcessRoleResource> usersProcessRoles = processRoleService.getByUserId(user.getId());

        Map<Long, ProcessRoleResource> inProgressProcessRoles = simpleToMap(inProgress, ApplicationResource::getId,
                applicationResource -> usersProcessRoles.stream()
                        .filter(processRoleResource -> processRoleResource.getApplicationId().equals(applicationResource.getId()))
                        .findFirst()
                        .orElse(null)
        );

        List<Long> applicationsAssigned = getAssignedApplications(inProgressProcessRoles);
        List<Long> leadApplicantApplications = getLeadApplicantApplications(inProgressProcessRoles);

        Map<Long, CompetitionResource> competitionApplicationMap = createCompetitionMap(user.getId(), inProgress, finished, getApplicationsForProjectsInSetup(projectsInSetup));

        return new ApplicantDashboardViewModel(applicationProgress, inProgress,
                applicationsAssigned, finished,
                projectsInSetup, competitionApplicationMap, applicationStatusMap, leadApplicantApplications);
    }

    private List<Long> getAssignedApplications(Map<Long, ProcessRoleResource> inProgressProcessRoles) {
        return inProgressProcessRoles.entrySet().stream().filter(entry -> {
            if (!UserRoleType.LEADAPPLICANT.getName().equals(entry.getValue().getRoleName())) {
                int count = applicationRestService.getAssignedQuestionsCount(entry.getKey(), entry.getValue().getId())
                        .getSuccessObjectOrThrowException();
                return count != 0;
            } else {
                return false;
            }
        }).map(Map.Entry::getKey).collect(toList());
    }

    private List<Long> getLeadApplicantApplications(Map<Long, ProcessRoleResource> inProgressProcessRoles) {
        return inProgressProcessRoles.entrySet().stream()
                .filter(entry -> UserRoleType.LEADAPPLICANT.getName().equals(entry.getValue().getRoleName()))
                .map(Map.Entry::getKey).collect(toList());
    }

    private Map<Long, ApplicationState> createApplicationStateMap(List<ApplicationResource> resources) {
        return simpleToMap(resources, ApplicationResource::getId, ApplicationResource::getApplicationState);
    }

    private boolean applicationInProgress(ApplicationResource a) {
        return (applicationStateInProgress(a) && competitionOpen(a))
                || (applicationStateSubmitted(a) && competitionFundingNotYetComplete(a));
    }

    private boolean applicationFinished(ApplicationResource a) {
        return (applicationStateFinished(a))
                || (applicationStateInProgress(a) && competitionClosed(a))
                || (applicationStateSubmitted(a) && competitionFundingComplete(a));
    }

    private boolean applicationStateInProgress(ApplicationResource a) {
        return inProgress.contains(a.getApplicationState());
    }

    private boolean competitionOpen(ApplicationResource a) {
        return open.contains(a.getCompetitionStatus());
    }

    private boolean competitionClosed(ApplicationResource a) {
        return !competitionOpen(a);
    }

    private boolean applicationStateSubmitted(ApplicationResource a) {
        return submitted.contains(a.getApplicationState());
    }

    private boolean applicationStateFinished(ApplicationResource a) {
        return finished.contains(a.getApplicationState());
    }

    private boolean competitionFundingNotYetComplete(ApplicationResource a) {
        return fundingNotYetCompete.contains(a.getCompetitionStatus());
    }

    private boolean competitionFundingComplete(ApplicationResource a) {
        return fundingComplete.contains(a.getCompetitionStatus());
    }

    private List<ApplicationResource> getApplicationsForProjectsInSetup(List<ProjectResource> resources) {
        return resources.stream().map(project -> applicationService.getById(project.getApplication())).collect(Collectors.toList());
    }

    @SafeVarargs
    private final Map<Long, CompetitionResource> createCompetitionMap(Long userId, List<ApplicationResource>... resources) {
        List<CompetitionResource> allUserCompetitions = competitionsRestService.getCompetitionsByUserId(userId).getSuccessObjectOrThrowException();

        return combineLists(resources).stream()
                .collect(
                        Collectors.toMap(
                                ApplicationResource::getId,
                                application -> allUserCompetitions.stream()
                                        .filter(competitionResource -> competitionResource.getId().equals(application.getCompetition()))
                                        .findFirst()
                                        .orElse(null), (p1, p2) -> p1)
                );
    }
}
