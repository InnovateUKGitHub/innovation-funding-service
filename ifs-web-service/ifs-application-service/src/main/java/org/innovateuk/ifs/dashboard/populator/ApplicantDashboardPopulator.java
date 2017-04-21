package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionService competitionService;


    public ApplicantDashboardViewModel populate(UserResource user) {
        List<ApplicationResource> allApplications = applicationRestService
                .getApplicationsByUserId(user.getId())
                .getSuccessObjectOrThrowException();

        Map<Long, CompetitionResource> competitionApplicationMap = createCompetitionApplicationMap(allApplications);
        Map<Long, ApplicationState> applicationStatusMap = createApplicationStateMap(allApplications);

        List<ApplicationResource> inProgress =
                allApplications.stream()
                        .filter(this::applicationInProgress)
                        .collect(toList());

        Map<Long, Integer> applicationProgress =
                inProgress.stream()
                        .collect(toMap(ApplicationResource::getId, a -> a.getCompletion().intValue()));

        List<ApplicationResource> finished =
                allApplications.stream()
                        .filter(this::applicationFinished)
                        .collect(toList());

        List<ProjectResource> projectsInSetup = projectService.findByUser(user.getId()).getSuccessObject();

        List<Long> applicationsAssigned = getAssignedApplications(inProgress, user);

        return new ApplicantDashboardViewModel(applicationProgress, inProgress,
                applicationsAssigned, finished,
                projectsInSetup, competitionApplicationMap, applicationStatusMap);
    }

    private List<Long> getAssignedApplications(List<ApplicationResource> inProgress, UserResource user) {
        return inProgress.stream().filter(applicationResource -> {
                    ProcessRoleResource role = processRoleService.findProcessRole(user.getId(), applicationResource.getId());
                    if (!UserRoleType.LEADAPPLICANT.getName().equals(role.getRoleName())) {
                        int count = applicationRestService
                                .getAssignedQuestionsCount(applicationResource.getId(), role.getId())
                                .getSuccessObjectOrThrowException();
                        return count != 0;
                    } else {
                        return false;
                    }
                }
        ).mapToLong(ApplicationResource::getId).boxed().collect(toList());
    }

    private Map<Long, ApplicationState> createApplicationStateMap(List<ApplicationResource> resources) {
        return resources.stream()
                .collect(toMap(
                        ApplicationResource::getId,
                        ApplicationResource::getApplicationState
                ));
    }

    private Map<Long, CompetitionResource> createCompetitionApplicationMap(List<ApplicationResource> resources) {
        Map<Long, CompetitionResource> competitions = getCompetitions(resources);
        return resources.stream()
                .collect(
                        toMap(
                                ApplicationResource::getId,
                                application -> competitions.get(application.getCompetition()), (p1, p2) -> p1)
                );
    }

    private Map<Long, CompetitionResource> getCompetitions(List<ApplicationResource> resources) {
        return resources.stream()
                .map(ApplicationResource::getCompetition)
                .distinct()
                .map(compId -> competitionService.getById(compId))
                .collect(toMap(
                        CompetitionResource::getId,
                        identity()
                ));
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
}
