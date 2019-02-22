package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.dashboard.viewmodel.*;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    private ApplicationRestService applicationRestService;
    private UserRestService userRestService;
    private ProjectRestService projectRestService;
    private CompetitionRestService competitionRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;

    public ApplicantDashboardPopulator(ApplicationRestService applicationRestService,
                                       UserRestService userRestService,
                                       ProjectRestService projectRestService,
                                       CompetitionRestService competitionRestService,
                                       InterviewAssignmentRestService interviewAssignmentRestService) {
        this.applicationRestService = applicationRestService;
        this.userRestService = userRestService;
        this.projectRestService = projectRestService;
        this.competitionRestService = competitionRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
    }

    public ApplicantDashboardViewModel populate(Long userId, String originQuery) {
        List<ProcessRoleResource> usersProcessRoles = getUserProcessRolesWithApplicationRole(userId);
        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        List<ApplicationResource> allApplications = applicationRestService.getApplicationsByUserId(userId).getSuccess();
        Map<Long, CompetitionResource> competitionsById = getAllCompetitionsForUser(allApplications);
        List<ApplicationResource> applications = applications(allApplications, usersProcessRoles, competitionsById);
        List<ApplicationResource> grantTransfers = grantTransfers(allApplications, usersProcessRoles, competitionsById);
        List<ProjectResource> allProjects = projectRestService.findByUserId(userId).getSuccess();
        List<ProjectResource> projectsInSetup = getNonWithdrawnProjects(allProjects);
        Map<Long, ProjectResource> euGrantTransferProjects = new HashMap<>();

        List<ProjectDashboardRowViewModel> projectViews = projectsInSetup.stream().map(project -> {
            ApplicationResource application = applicationRestService.getApplicationById(project.getApplication()).getSuccess();
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            if (competition.isH2020()) {
                euGrantTransferProjects.put(application.getId(), project);
                return null;
            }
            return new ProjectDashboardRowViewModel(project.getName(), project.getApplication(), competition.getName(), project.getId(), project.getName());
        }).filter(Objects::nonNull).sorted().collect(toList());

        List<InProgressDashboardRowViewModel> inProgressViews = applications.stream()
                .filter(this::applicationInProgress)
                .map(application -> {
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            Optional<ProcessRoleResource> role = usersProcessRoles.stream()
                    .filter(processRoleResource -> processRoleResource.getApplicationId().equals(application.getId()))
                    .findFirst();
            boolean invitedToInterview = interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess();
            return new InProgressDashboardRowViewModel(application.getName(), application.getId(), competition.getName(),
                    isAssigned(application, role), application.getApplicationState(), isLead(role), competition.getEndDate(),
                    competition.getDaysLeft(), application.getCompletion().intValue(), invitedToInterview);
        }).sorted().collect(toList());

        List<PreviousDashboardRowViewModel> previousViews =
                applications
                        .stream()
                        .filter(this::applicationFinished)
                        .map(application -> new PreviousDashboardRowViewModel(application.getName(),
                                                                          application.getId(),
                                                                          application.getCompetitionName(),
                                                                          application.getApplicationState()))
                        .sorted()
                        .collect(toList());

        List<EuGrantTransferDashboardRowViewModel> grantTransferViews = grantTransfers.stream()
                .map(application -> {
                    CompetitionResource competition = competitionsById.get(application.getCompetition());
                    return new EuGrantTransferDashboardRowViewModel(application.getName(), application.getId(), competition.getName(),
                            application.getApplicationState(), application.getCompletion().intValue(),
                            Optional.ofNullable(euGrantTransferProjects.get(application.getId())).map(ProjectResource::getId).orElse(null));
                }).sorted().collect(toList());

        return new ApplicantDashboardViewModel(projectViews, grantTransferViews, inProgressViews, previousViews, originQuery, user.hasRole(MONITORING_OFFICER));
    }

    private boolean isLead(Optional<ProcessRoleResource> processRole) {
        return processRole.map(ProcessRoleResource::getRole).map(Role::isLeadApplicant).orElse(false);
    }

    private boolean isAssigned(ApplicationResource application, Optional<ProcessRoleResource> processRole) {
        if (processRole.isPresent() && !isLead(processRole)) {
            int count = applicationRestService.getAssignedQuestionsCount(application.getId(), processRole.get().getId())
                    .getSuccess();
            return count != 0;
        } else {
            return false;
        }
    }

    private List<ApplicationResource> applications(List<ApplicationResource> applications, List<ProcessRoleResource> usersProcessRoles, Map<Long, CompetitionResource> competitionsById) {

        List<Long> usersProcessRolesApplicationIds = simpleMap(
                usersProcessRoles,
                ProcessRoleResource::getApplicationId
        );

        return simpleFilter(
                applications,
                appResource -> usersProcessRolesApplicationIds.contains(appResource.getId())
                        && !competitionsById.get(appResource.getCompetition()).isH2020()
        );
    }

    private List<ApplicationResource> grantTransfers(List<ApplicationResource> applications, List<ProcessRoleResource> usersProcessRoles, Map<Long, CompetitionResource> competitionsById) {

        List<Long> usersProcessRolesApplicationIds = simpleMap(
                usersProcessRoles,
                ProcessRoleResource::getApplicationId
        );

        return simpleFilter(
                applications,
                appResource -> usersProcessRolesApplicationIds.contains(appResource.getId())
                        && competitionsById.get(appResource.getCompetition()).isH2020()
        );
    }

    private List<ProcessRoleResource> getUserProcessRolesWithApplicationRole(Long userId) {

        return simpleFilter(
                userRestService.findProcessRoleByUserId(userId).getSuccess(),
                this::hasAnApplicantRole
        );
    }

    private List<ProjectResource> getNonWithdrawnProjects(List<ProjectResource> allProjects) {
        return simpleFilter(
                allProjects,
                projectResource -> !projectResource.isWithdrawn()
        );
    }

    private boolean hasAnApplicantRole(ProcessRoleResource processRoleResource) {
        return processRoleResource.getRole() == APPLICANT ||
                processRoleResource.getRole() == LEADAPPLICANT ||
                processRoleResource.getRole() == COLLABORATOR;
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
        return ApplicationState.submittedStates.contains(application.getApplicationState());
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

    private Map<Long, CompetitionResource> getAllCompetitionsForUser(List<ApplicationResource> userApplications) {
        List<Long> competitionIdsForUser = userApplications.stream()
                .map(ApplicationResource::getCompetition)
                .distinct()
                .collect(Collectors.toList());

        List<CompetitionResource> competitions =  simpleMap(competitionIdsForUser, id -> competitionRestService.getCompetitionById(id).getSuccess());
        return simpleToMap(competitions, CompetitionResource::getId, Function.identity());
    }
}
