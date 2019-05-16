package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource.ApplicantDashboardResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.EU_GRANT_TRANSFER;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_PROGRESS;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_SETUP;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Transactional and secured service focused around the processing of Applications.
 */
@Service
public class ApplicationDashboardServiceImpl extends BaseTransactionalService implements ApplicationDashboardService {

    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private ProjectMapper projectMapper;
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
        List<ProjectResource> nonWithdrawnProjects = getNonWithdrawnProjectResources(userId);
        List<ProcessRoleResource> processRoles = usersRolesService.getProcessRolesByUserId(userId).getSuccess();
        List<ProjectResource> projects = projectService.findByUserId(userId).getSuccess();
        List<ApplicationResource> applications = findByUserId(userId).getSuccess();
        Map<Long, CompetitionResource> competitionsById = getCompetitionsById(applications, projects);

        List<DashboardApplicationInSetupResource> inSetup = getUserApplicationsInSetup(nonWithdrawnProjects, competitionsById);
        List<DashboardApplicationForEuGrantTransferResource> euGrantTransfer = getUserApplicationsForEuGrantTransfers(nonWithdrawnProjects, processRoles, competitionsById, applications);
        List<DashboardApplicationInProgressResource> inProgress = getUserApplicationsInProgress(processRoles, competitionsById, applications);
        List<DashboardPreviousApplicationResource> previous = getUserPreviousApplications(userId, processRoles, projects, competitionsById);

        ApplicantDashboardResource applicantDashboardResource = new ApplicantDashboardResourceBuilder()
                .withInSetup(inSetup)
                .withEuGrantTransfer(euGrantTransfer)
                .withInProgress(inProgress)
                .withPrevious(previous)
                .build();

        return serviceSuccess(applicantDashboardResource);
    }

    private List<DashboardApplicationInSetupResource> getUserApplicationsInSetup(List<ProjectResource> nonWithdrawnProjects, Map<Long, CompetitionResource> competitionsById) {
        return nonWithdrawnProjects
                .stream()
                .map(project -> {
                    CompetitionResource competition = competitionsById.get(project.getCompetition());
                    if (competition.isH2020()) {
                        return null;
                    }
                    return new DashboardApplicationInSetupResource.DashboardApplicationInSetupResourceBuilder()
                            .withTitle(project.getName())
                            .withApplicationId(project.getApplication())
                            .withCompetitionTitle(competition.getName())
                            .withProjectId(project.getId())
                            .withProjectTitle(project.getName())
                            .withDashboardSection(IN_SETUP)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(toList());
    }

    private List<DashboardApplicationForEuGrantTransferResource> getUserApplicationsForEuGrantTransfers(List<ProjectResource> nonWithdrawnProjectResources, List<ProcessRoleResource> processRoles, Map<Long, CompetitionResource> competitionsById, List<ApplicationResource> applications) {
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

        Map<Long, ProjectResource> euGrantTransferProjects = nonWithdrawnProjectResources
                .stream()
                .filter(projectResource -> projectIsForEuGrantFundingCompetition(projectResource, competitionsById))
                .collect(toMap(ProjectResource::getApplication, identity()));

        return grantTransfers
                .stream()
                .map(application -> {
                    CompetitionResource competition = competitionsById.get(application.getCompetition());
                    Long projectId = Optional.ofNullable(euGrantTransferProjects.get(application.getId())).map(ProjectResource::getId).orElse(null);
                    return new DashboardApplicationForEuGrantTransferResource.DashboardApplicationForEuGrantTransferResourceBuilder()
                            .withTitle(application.getName())
                            .withApplicationId(application.getId())
                            .withCompetitionTitle(competition.getName())
                            .withApplicationState(application.getApplicationState())
                            .withApplicationProgress(application.getCompletion().intValue())
                            .withProjectId(projectId)
                            .withDashboardSection(EU_GRANT_TRANSFER)
                            .build();
                })
                .sorted()
                .collect(toList());
    }

    private List<DashboardApplicationInProgressResource> getUserApplicationsInProgress(List<ProcessRoleResource> processRoles, Map<Long, CompetitionResource> competitionsById, List<ApplicationResource> applications) {
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
                            .build();
                })
                .sorted()
                .collect(toList());
    }

    private List<DashboardPreviousApplicationResource> getUserPreviousApplications(Long userId, List<ProcessRoleResource> processRoles, List<ProjectResource> projects, Map<Long, CompetitionResource> competitionsById) {
        List<ApplicationResource> nonH2020ApplicationsForUser = filterApplicationsForUserForNonH2020Competitions(userId, processRoles, projects, competitionsById);

        return nonH2020ApplicationsForUser
                .stream()
                .filter(this::applicationFinished)
                .map(application -> new DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder()
                        .withTitle(application.getName())
                        .withApplicationId(application.getId())
                        .withCompetitionTitle(application.getCompetitionName())
                        .withApplicationState(application.getApplicationState())
                        .withDashboardSection(PREVIOUS)
                        .build())
                .sorted()
                .collect(toList());
    }

    private List<ApplicationResource> filterApplicationsForUserForNonH2020Competitions(Long userId, List<ProcessRoleResource> processRoles, List<ProjectResource> projects, Map<Long, CompetitionResource> competitionsById) {
        List<Long> usersProcessRolesApplicationIds = processRoles
                .stream()
                .filter(this::hasAnApplicantRole)
                .map(ProcessRoleResource::getApplicationId)
                .collect(toList());

        List<ApplicationResource> applicationResources = findByUserId(userId).getSuccess();

        return applicationResources
                .stream()
                .filter(application -> {
                    List<Long> competitionIdsForUser = concat(
                            applicationResources.stream().map(ApplicationResource::getCompetition),
                            projects.stream().map(ProjectResource::getCompetition)
                    )
                            .distinct()
                            .collect(toList());

                    List<CompetitionResource> competitions =  competitionIdsForUser
                            .stream()
                            .map(competitionsById::get)
                            .collect(toList());

                    return usersProcessRolesApplicationIds.contains(application.getId())
                            && !simpleToMap(competitions, CompetitionResource::getId, Function.identity()).get(application.getCompetition()).isH2020();
                })
                .collect(toList());

    }

    private boolean isAssigned(ApplicationResource application, Optional<ProcessRoleResource> processRole) {
        if (processRole.isPresent() && !isLead(processRole)) {
            int count = questionStatusService.getCountByApplicationIdAndAssigneeId(application.getId(), processRole.get().getId()).getSuccess();
            return count != 0;
        } else {
            return false;
        }
    }

    private boolean isLead(Optional<ProcessRoleResource> processRole) {
        return processRole.map(ProcessRoleResource::getRole).map(Role::isLeadApplicant).orElse(false);
    }

    private boolean applicationInProgress(ApplicationResource application) {
        return (applicationStateInProgress(application) && competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingNotYetComplete(application));
    }

    private boolean competitionOpen(ApplicationResource application) {
        return application.getCompetitionStatus().equals(CompetitionStatus.OPEN);
    }

    private boolean applicationStateInProgress(ApplicationResource application) {
        return ApplicationState.inProgressStates.contains(application.getApplicationState());
    }

    private boolean applicationStateSubmitted(ApplicationResource application) {
        return ApplicationState.submittedStates.contains(application.getApplicationState());
    }

    private boolean competitionFundingNotYetComplete(ApplicationResource application) {
        return CompetitionStatus.fundingNotCompleteStatuses.contains(application.getCompetitionStatus());
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

    private List<ProjectResource> getNonWithdrawnProjectResources(long userId) {
        return projectUserRepository.findByUserId(userId)
                .stream()
                .map(ProjectUser::getProcess)
                .distinct()
                .map(projectMapper::mapToResource)
                .filter(projectResource -> !projectResource.isWithdrawn())
                .collect(toList());
    }

    private boolean applicationFinished(ApplicationResource application) {
        return (applicationStateFinished(application))
                || (applicationStateInProgress(application) && !competitionOpen(application))
                || (applicationStateSubmitted(application) && competitionFundingComplete(application));
    }

    private boolean applicationStateFinished(ApplicationResource application) {
        return ApplicationState.finishedStates.contains(application.getApplicationState());
    }

    private boolean competitionFundingComplete(ApplicationResource application) {
        return CompetitionStatus.fundingCompleteStatuses.contains(application.getCompetitionStatus());
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
