package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;


    public ApplicantDashboardViewModel populate(Long userId) {
        List<ProcessRoleResource> usersProcessRoles = getUserProcessRolesWithApplicationRole(userId);
        List<ApplicationResource> allApplications = getAllApplicationsAsApplicant(userId, usersProcessRoles);
        List<ProjectResource> projectsInSetup = projectService.findByUser(userId).getSuccess();

        Map<Long, ApplicationState> applicationStatusMap = createApplicationStateMap(allApplications);
        List<ApplicationResource> inProgressApplications = simpleFilter(allApplications, this::applicationInProgress);

        Map<Long, Integer> applicationProgress =
                simpleToMap(inProgressApplications, ApplicationResource::getId, a -> a.getCompletion().intValue());
        List<ApplicationResource> finishedApplications = simpleFilter(allApplications, this::applicationFinished);

        Map<Long, Optional<ProcessRoleResource>> inProgressProcessRoles = simpleToMap(
                inProgressApplications,
                ApplicationResource::getId,
                applicationResource -> getInProgressProcessRoleResource(applicationResource, usersProcessRoles)
                );

        List<Long> applicationsAssigned = getAssignedApplications(inProgressProcessRoles);
        List<Long> leadApplicantApplications = getLeadApplicantApplications(inProgressProcessRoles);

        Map<Long, CompetitionResource> competitionApplicationMap = createCompetitionMap(userId, inProgressApplications, finishedApplications, getApplicationsForProjectsInSetup(projectsInSetup));

        return new ApplicantDashboardViewModel(applicationProgress,
                                               inProgressApplications,
                                               applicationsAssigned,
                                               finishedApplications,
                                               projectsInSetup,
                                               competitionApplicationMap,
                                               applicationStatusMap,
                                               leadApplicantApplications);
    }

    private List<ApplicationResource> getAllApplicationsAsApplicant(Long userId, List<ProcessRoleResource> usersProcessRoles) {

        List<Long> usersProcessRolesApplicationIds = simpleMap(
                usersProcessRoles,
                ProcessRoleResource::getApplicationId
        );

        return simpleFilter(
                applicationRestService.getApplicationsByUserId(userId).getSuccess(),
                appResource -> usersProcessRolesApplicationIds.contains(appResource.getId())
        );
    }

    private List<ProcessRoleResource> getUserProcessRolesWithApplicationRole(Long userId) {

        return simpleFilter(
                processRoleService.getByUserId(userId),
                this::hasAnApplicantRole
        );
    }

    private boolean hasAnApplicantRole(ProcessRoleResource processRoleResource) {
        return processRoleResource.getRole() == Role.APPLICANT.getId() ||
                processRoleResource.getRole() == LEADAPPLICANT.getId() ||
                processRoleResource.getRole() == COLLABORATOR.getId();
    }



    private Optional<ProcessRoleResource> getInProgressProcessRoleResource(ApplicationResource applicationResource, List<ProcessRoleResource> processRoleResources) {
        return simpleFindFirst(
                processRoleResources,
                processRoleResource -> processRoleResource.getApplicationId().equals(applicationResource.getId())
        );
    }

    private List<Long> getAssignedApplications(Map<Long, Optional<ProcessRoleResource>> inProgressProcessRoles) {
        return inProgressProcessRoles
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPresent())
                .filter(entry -> !LEADAPPLICANT.getName().equals(entry.getValue().get().getRoleName()))
                .filter(entry -> applicationRestService.getAssignedQuestionsCount(entry.getKey(), entry.getValue().get().getId()).getSuccess() > 0)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private List<Long> getLeadApplicantApplications(Map<Long, Optional<ProcessRoleResource>> inProgressProcessRoles) {
        return inProgressProcessRoles
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPresent()
                        && LEADAPPLICANT.getId() == entry.getValue().get().getRole())
                .map(Map.Entry::getKey).collect(toList());
    }

    private Map<Long, ApplicationState> createApplicationStateMap(List<ApplicationResource> resources) {
        return simpleToMap(resources, ApplicationResource::getId, ApplicationResource::getApplicationState);
    }

    private boolean applicationInProgress(ApplicationResource application) {
        return (applicationStateInProgress(application) && competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingNotYetComplete(application));
    }

    private boolean applicationFinished(ApplicationResource application) {
        return (applicationStateFinished(application))
                || (applicationStateInProgress(application) && !competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingComplete(application));
    }

    private boolean applicationStateInProgress(ApplicationResource application) {
        return ApplicationState.inProgressStates.contains(application.getApplicationState());
    }

    private boolean competitionOpen(ApplicationResource application) {
        return CompetitionStatus.OPEN.equals(application.getCompetitionStatus());
    }

    private boolean applicationStateSubmitted(ApplicationResource application) {
        return ApplicationState.blahStates.contains(application.getApplicationState());
    }

    private boolean applicationStateFinished(ApplicationResource application) {
        return ApplicationState.finishedStates.contains(application.getApplicationState());
    }

    private boolean competitionFundingNotYetComplete(ApplicationResource application) {
        return CompetitionStatus.fundingNotCompleteStatuses.contains(application.getCompetitionStatus());
    }

    private boolean competitionFundingComplete(ApplicationResource application) {
        return CompetitionStatus.fundingCompleteStatuses.contains(application.getCompetitionStatus());
    }

    private List<ApplicationResource> getApplicationsForProjectsInSetup(List<ProjectResource> resources) {

        return simpleMap(
                resources,
                project -> applicationService.getById(project.getApplication())
        );
    }

    private CompetitionResource getCompetitionFromApplication(ApplicationResource application,
                                                              List<CompetitionResource> competitions) {
        return simpleFindFirst(
                competitions,
                comp -> comp.getId().equals(application.getCompetition())
        ).orElse(null);
    }

    @SafeVarargs
    private final Map<Long, CompetitionResource> createCompetitionMap(Long userId, List<ApplicationResource>... resources) {
        List<CompetitionResource> allUserCompetitions = competitionRestService.getCompetitionsByUserId(userId).getSuccess();

        return simpleToMap(
                combineLists(resources),
                ApplicationResource::getId,
                application -> getCompetitionFromApplication(application, allUserCompetitions)
                );
    }
}
