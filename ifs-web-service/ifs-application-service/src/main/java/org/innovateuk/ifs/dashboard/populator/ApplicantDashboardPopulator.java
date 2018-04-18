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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    private EnumSet<ApplicationState> inProgress = EnumSet.of(ApplicationState.CREATED, ApplicationState.OPEN);
    private EnumSet<ApplicationState> submitted = EnumSet.of(ApplicationState.SUBMITTED, ApplicationState.INELIGIBLE);
    private EnumSet<ApplicationState> finished = EnumSet.of(ApplicationState.APPROVED, ApplicationState.REJECTED, ApplicationState.INELIGIBLE_INFORMED);
    private EnumSet<CompetitionStatus> fundingNotYetCompete = EnumSet.of(CompetitionStatus.OPEN, CompetitionStatus.CLOSED, CompetitionStatus.IN_ASSESSMENT, CompetitionStatus.FUNDERS_PANEL);
    private EnumSet<CompetitionStatus> fundingComplete = EnumSet.of(CompetitionStatus.ASSESSOR_FEEDBACK, CompetitionStatus.PROJECT_SETUP);
    private EnumSet<CompetitionStatus> open = EnumSet.of(CompetitionStatus.OPEN);

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
        List<ProjectResource> projectsInSetup = projectService.findByUser(userId).getSuccess();
        Map<Long, CompetitionResource> competitionsById = getAllCompetitionsForUser(userId);


        List<ProjectDashboardRowViewModel> projectViews = projectsInSetup.stream().map(project -> {
            ApplicationResource application = applicationRestService.getApplicationById(project.getApplication()).getSuccess();
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            return new ProjectDashboardRowViewModel(project.getName(), project.getApplication(), competition.getName(), project.getId(), project.getName());
        }).sorted().collect(toList());

        List<InProgressDashboardRowViewModel> inProgressViews = allApplications.stream().filter(this::applicationInProgress).map(application -> {
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            Optional<ProcessRoleResource> role = usersProcessRoles.stream()
                    .filter(processRoleResource -> processRoleResource.getApplicationId().equals(application.getId()))
                    .findFirst();
            boolean invitedToInterview = interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess();
            return new InProgressDashboardRowViewModel(application.getName(), application.getId(), competition.getName(),
                    isAssigned(application, role), application.getApplicationState(), isLead(role), competition.getEndDate(),
                    competition.getDaysLeft(), application.getCompletion().intValue(), invitedToInterview);
        }).sorted().collect(toList());

        List<PreviousDashboardRowViewModel> previousViews = allApplications.stream().filter(this::applicationFinished).map(application -> {
            CompetitionResource competition = competitionsById.get(application.getCompetition());
            return new PreviousDashboardRowViewModel(application.getName(), application.getId(), competition.getName(), application.getApplicationState());
        }).sorted().collect(toList());


        return new ApplicantDashboardViewModel(projectViews, inProgressViews, previousViews);
    }

    private boolean isLead(Optional<ProcessRoleResource> processRole) {
        return processRole.map(ProcessRoleResource::getRole).map(Role::getById).map(Role::isLeadApplicant).orElse(false);
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
        List<Long> usersProcessRolesApplicationIds = usersProcessRoles.stream()
                .map(processRoleResource -> processRoleResource.getApplicationId())
                .collect(toList());

        return applicationRestService.getApplicationsByUserId(userId)
                .getSuccess()
                .stream()
                .filter(applicationResource -> usersProcessRolesApplicationIds.contains(applicationResource.getId()))
                .collect(toList());
    }

    private List<ProcessRoleResource> getUserProcessRolesWithApplicationRole(Long userId) {
        return processRoleService.getByUserId(userId)
                .stream()
                .filter(processRoleResource -> hasAnApplicantRole(processRoleResource))
                .collect(toList());
    }

    private boolean hasAnApplicantRole(ProcessRoleResource processRoleResource) {
        return processRoleResource.getRole() == Role.APPLICANT.getId() ||
                processRoleResource.getRole() == LEADAPPLICANT.getId() ||
                processRoleResource.getRole() == COLLABORATOR.getId();
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
