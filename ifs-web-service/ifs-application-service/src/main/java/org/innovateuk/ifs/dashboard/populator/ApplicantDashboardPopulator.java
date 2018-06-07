package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InProgressDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.PreviousDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.ProjectDashboardRowViewModel;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    public ApplicantDashboardViewModel populate(Long userId) {
        List<ProcessRoleResource> usersProcessRoles = getUserProcessRolesWithApplicationRole(userId);
        List<ApplicationResource> allApplications = getAllApplicationsAsApplicant(userId, usersProcessRoles);
        List<ProjectResource> allProjects = projectService.findByUser(userId).getSuccess();
        Map<Long, CompetitionResource> competitionsById = getAllCompetitionsForUser(userId);
        List<ProjectResource> projectsInSetup = getNonWithdrawnProjects(allProjects);

        List<ProjectDashboardRowViewModel> projectViews = projectsInSetup.stream().map(project -> {
            ApplicationResource application = applicationRestService.getApplicationById(project.getApplication()).getSuccess();
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            return new ProjectDashboardRowViewModel(project.getName(), project.getApplication(), competition.getName(), project.getId(), project.getName());
        }).sorted().collect(toList());

        List<InProgressDashboardRowViewModel> inProgressViews = allApplications.stream()
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
                allApplications
                        .stream()
                        .filter(this::applicationFinished)
                        .map(application -> new PreviousDashboardRowViewModel(application.getName(),
                                                                          application.getId(),
                                                                          application.getCompetitionName(),
                                                                          application.getApplicationState()))
                        .sorted()
                        .collect(toList());

        return new ApplicantDashboardViewModel(projectViews, inProgressViews, previousViews);
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

    private Map<Long, CompetitionResource> getAllCompetitionsForUser(Long userId) {
        List<ApplicationResource> userApplications = applicationRestService.getApplicationsByUserId(userId).getSuccess();
        List<Long> competitionIdsForUser = userApplications.stream()
                .map(ApplicationResource::getCompetition)
                .distinct()
                .collect(Collectors.toList());

        List<CompetitionResource> competitions =  simpleMap(competitionIdsForUser, id -> competitionRestService.getCompetitionById(id).getSuccess());
        return simpleToMap(competitions, CompetitionResource::getId, Function.identity());
    }
}
