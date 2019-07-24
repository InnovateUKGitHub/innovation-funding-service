package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.*;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.concat;
import static org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource.ApplicantDashboardResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInProgressRowResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.*;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

/**
 * Transactional and secured service that generates a dashboard of applications for a user.
 */
@Service
public class ApplicationDashboardServiceImpl extends BaseTransactionalService implements ApplicationDashboardService {

    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private InterviewAssignmentService interviewAssignmentService;
    @Autowired
    private QuestionStatusService questionStatusService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CompetitionService competitionService;

    @Override
    public ServiceResult<ApplicantDashboardResource> getApplicantDashboard(long userId) {
        List<ProjectResource> projects = projectService.findByUserId(userId).getSuccess();
        List<ProcessRoleResource> processRoles = usersRolesService.getProcessRolesByUserId(userId).getSuccess();
        List<ApplicationResource> applications = findByUserId(userId).getSuccess();
        Map<Long, CompetitionResource> competitionsById = getCompetitionsById(applications, projects);

        List<DashboardInSetupRowResource> inSetup = getUserApplicationsInSetup(projects, competitionsById);
        List<DashboardEuGrantTransferRowResource> euGrantTransfer = getUserApplicationsForEuGrantTransfers(projects, processRoles, applications, competitionsById);
        List<DashboardInProgressRowResource> inProgress = getUserApplicationsInProgress(processRoles, competitionsById, applications);
        List<DashboardPreviousRowResource> previous = getUserPreviousApplications(applications, projects, processRoles, competitionsById);

        ApplicantDashboardResource applicantDashboardResource = new ApplicantDashboardResourceBuilder()
                .withInSetup(inSetup)
                .withEuGrantTransfer(euGrantTransfer)
                .withInProgress(inProgress)
                .withPrevious(previous)
                .build();

        return serviceSuccess(applicantDashboardResource);
    }

    private List<DashboardInSetupRowResource> getUserApplicationsInSetup(List<ProjectResource> projects, Map<Long, CompetitionResource> competitionsById) {
        return projects
                .stream()
                .filter(isNotWithdrawn())
                .map(project -> {
                    CompetitionResource competition = competitionsById.get(project.getCompetition());
                    if (competition.isH2020()) {
                        return null;
                    }
                    return new DashboardInSetupRowResource.DashboardApplicationInSetupResourceBuilder()
                            .withTitle(project.getName())
                            .withApplicationId(project.getApplication())
                            .withCompetitionTitle(competition.getName())
                            .withProjectId(project.getId())
                            .withProjectTitle(project.getName())
                            .withDashboardSection(IN_SETUP)
                            .withTargetStartDate(project.getTargetStartDate())
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(toList());
    }

    private List<DashboardEuGrantTransferRowResource> getUserApplicationsForEuGrantTransfers(List<ProjectResource> projects, List<ProcessRoleResource> processRoles, List<ApplicationResource> applications, Map<Long, CompetitionResource> competitionsById) {
        List<Long> applicantProcessRoleIds = processRoles
                .stream()
                .filter(this::hasAnApplicantRole)
                .map(ProcessRoleResource::getApplicationId)
                .collect(toList());

        List<ApplicationResource> grantTransfers = applications
                .stream()
                .filter(application -> applicantProcessRoleIds.contains(application.getId()))
                .filter(application -> competitionsById.get(application.getCompetition()).isH2020())
                .collect(toList());

        Map<Long, ProjectResource> euGrantTransferProjects = projects
                .stream()
                .filter(isNotWithdrawn())
                .filter(projectResource -> projectIsForEuGrantFundingCompetition(projectResource, competitionsById))
                .collect(toMap(ProjectResource::getApplication, identity()));

        return grantTransfers
                .stream()
                .map(application -> {
                    CompetitionResource competition = competitionsById.get(application.getCompetition());
                    Long projectId = Optional.ofNullable(euGrantTransferProjects.get(application.getId())).map(ProjectResource::getId).orElse(null);
                    return new DashboardEuGrantTransferRowResource.DashboardApplicationForEuGrantTransferResourceBuilder()
                            .withTitle(application.getName())
                            .withApplicationId(application.getId())
                            .withCompetitionTitle(competition.getName())
                            .withApplicationState(application.getApplicationState())
                            .withApplicationProgress(application.getCompletion().intValue())
                            .withProjectId(projectId)
                            .withDashboardSection(EU_GRANT_TRANSFER)
                            .withStartDate(application.getStartDate())
                            .build();
                })
                .sorted()
                .collect(toList());
    }

    private List<DashboardInProgressRowResource> getUserApplicationsInProgress(List<ProcessRoleResource> processRoles, Map<Long, CompetitionResource> competitionsById, List<ApplicationResource> applications) {
        List<ProcessRoleResource> applicantProcessRoles = processRoles
                .stream()
                .filter(this::hasAnApplicantRole)
                .collect(toList());

        List<Long> applicantProcessRoleIds = applicantProcessRoles
                .stream()
                .map(ProcessRoleResource::getApplicationId)
                .collect(toList());

        List<ApplicationResource> nonH2020Applications = applications
                .stream()
                .filter(application -> applicantProcessRoleIds.contains(application.getId()))
                .filter(application -> !competitionsById.get(application.getCompetition()).isH2020())
                .collect(toList());

        return nonH2020Applications
                .stream()
                .filter(this::applicationInProgress)
                .map(application -> {
                    CompetitionResource competition = competitionsById.get(application.getCompetition());
                    Optional<ProcessRoleResource> role = applicantProcessRoles.stream()
                            .filter(processRoleResource -> processRoleResource.getApplicationId().equals(application.getId()))
                            .findFirst();
                    boolean invitedToInterview = interviewAssignmentService.isApplicationAssigned(application.getId()).getSuccess();

                    return new DashboardApplicationInProgressResourceBuilder()
                            .withTitle(application.getName())
                            .withApplicationId(application.getId())
                            .withCompetitionTitle(competition.getName())
                            .withAssignedToMe(isAssigned(application, role))
                            .withApplicationState(application.getApplicationState())
                            .withLeadApplicant(isLead(role))
                            .withEndDate(competition.getEndDate())
                            .withDaysLeft(competition.getDaysLeft())
                            .withApplicationProgress(application.getCompletion().intValue())
                            .withAssignedToInterview(invitedToInterview)
                            .withDashboardSection(IN_PROGRESS)
                            .withStartDate(application.getStartDate())
                            .build();
                })
                .sorted()
                .collect(toList());
    }

    private List<DashboardPreviousRowResource> getUserPreviousApplications(List<ApplicationResource> applications,
                                                                           List<ProjectResource> projects,
                                                                           List<ProcessRoleResource> processRoles,
                                                                           Map<Long, CompetitionResource> competitionsById) {
        List<Long> usersProcessRolesApplicationIds = processRoles
                .stream()
                .filter(this::hasAnApplicantRole)
                .map(ProcessRoleResource::getApplicationId)
                .collect(toList());

        List<ApplicationResource> nonH2020ApplicationsForUser = applications
                .stream()
                .filter(application -> usersProcessRolesApplicationIds.contains(application.getId()))
                .filter(application -> !competitionsById.get(application.getCompetition()).isH2020())
                .collect(toList());

        return nonH2020ApplicationsForUser
                .stream()
                .filter(this::applicationFinished)
                .map(application -> new DashboardPreviousRowResource.DashboardPreviousApplicationResourceBuilder()
                        .withTitle(application.getName())
                        .withApplicationId(application.getId())
                        .withCompetitionTitle(application.getCompetitionName())
                        .withApplicationState(application.getApplicationState())
                        .withDashboardSection(PREVIOUS)
                        .withStartDate(application.getStartDate())
                        .withProjectId(getProjectId(application, projects))
                        .withProjectState(getProjectState(application, projects))
                        .build())
                .sorted()
                .filter(previousRow -> !previousRow.activeProject())
                .collect(toList());
    }

    private Long getProjectId(ApplicationResource application, List<ProjectResource> projects) {
        return projects
                .stream()
                .filter(p -> p.getApplication() == application.getId())
                .map(ProjectResource::getId)
                .findAny()
                .orElse(null);
    }

    private Optional<ProjectState> getProjectState(ApplicationResource application, List<ProjectResource> projects) {
        return projects
                .stream()
                .filter(p -> p.getApplication() == application.getId())
                .map(ProjectResource::getProjectState)
                .findAny();
    }

    private boolean isAssigned(ApplicationResource application, Optional<ProcessRoleResource> processRole) {
        if (processRole.isPresent() && !isLead(processRole)) {
            int count = questionStatusService.getCountByApplicationIdAndAssigneeId(application.getId(), processRole.get().getId()).getSuccess();
            return count != 0;
        } else {
            return false;
        }
    }

    private Predicate<ProjectResource> isNotWithdrawn() {
        return projectResource -> !projectResource.isWithdrawn();
    }

    private boolean isLead(Optional<ProcessRoleResource> processRole) {
        return processRole.map(ProcessRoleResource::getRole).map(Role::isLeadApplicant).orElse(false);
    }

    private boolean applicationInProgress(ApplicationResource application) {
        return (applicationStateInProgress(application) && competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingNotYetComplete(application));
    }

    private boolean competitionOpen(ApplicationResource application) {
        return application.getCompetitionStatus().equals(OPEN);
    }

    private boolean applicationStateInProgress(ApplicationResource application) {
        return inProgressStates.contains(application.getApplicationState());
    }

    private boolean applicationStateSubmitted(ApplicationResource application) {
        return submittedStates.contains(application.getApplicationState());
    }

    private boolean competitionFundingNotYetComplete(ApplicationResource application) {
        return fundingNotCompleteStatuses.contains(application.getCompetitionStatus());
    }

    private boolean hasAnApplicantRole(ProcessRoleResource processRoleResource) {
        return processRoleResource.getRole() == LEADAPPLICANT ||
                processRoleResource.getRole() == COLLABORATOR;
    }

    private boolean projectIsForEuGrantFundingCompetition(ProjectResource project, Map<Long, CompetitionResource> competitionsById) {
        CompetitionResource competition = competitionsById.get(project.getCompetition());
        return competition.isH2020();
    }

    private ServiceResult<List<ApplicationResource>> findByUserId(final Long userId) {
        return getUser(userId).andOnSuccessReturn(user -> {
            List<Application> applications = getApplicationsForUser(user);
            return applications
                    .stream()
                    .map(applicationMapper::mapToResource)
                    .collect(toList());
        });
    }

    private List<Application> getApplicationsForUser(User user) {
        return getApplicationIdsForUser(user)
                .stream()
                .map(appId -> appId != null ? applicationRepository.findById(appId).orElse(null) : null)
                .collect(toList());
    }

    private Set<Long> getApplicationIdsForUser(User user) {
        return processRoleRepository.findByUser(user)
                .stream()
                .map(ProcessRole::getApplicationId)
                .collect(toSet());
    }

    private boolean applicationFinished(ApplicationResource application) {
        return (applicationStateFinished(application))
                || (applicationStateInProgress(application) && !competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingComplete(application));
    }

    private boolean applicationStateFinished(ApplicationResource application) {
        return finishedStates.contains(application.getApplicationState());
    }

    private boolean competitionFundingComplete(ApplicationResource application) {
        return fundingCompleteStatuses.contains(application.getCompetitionStatus());
    }

    private Map<Long, CompetitionResource> getCompetitionsById(List<ApplicationResource> allApplicationsForUser, List<ProjectResource> allProjectsForUser) {
        List<Long> competitionIdsForUser = concat(
                allApplicationsForUser.stream().map(ApplicationResource::getCompetition),
                allProjectsForUser.stream().map(ProjectResource::getCompetition))
                .distinct()
                .collect(toList());

        return competitionIdsForUser
                .stream()
                .map(id -> competitionService.getCompetitionById(id).getSuccess())
                .collect(toList())
                .stream()
                .collect(toMap(CompetitionResource::getId, identity()));
    }
}
