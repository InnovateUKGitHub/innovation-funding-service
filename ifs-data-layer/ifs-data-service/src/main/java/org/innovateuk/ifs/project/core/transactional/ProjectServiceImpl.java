package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.BaseFailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProjectServiceImpl extends AbstractProjectServiceImpl implements ProjectService {

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private FinanceChecksGenerator financeChecksGenerator;

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public ServiceResult<ProjectResource> getProjectById(long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProjectResource> getByApplicationId(long applicationId) {
        return getProjectByApplication(applicationId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<Boolean> existsOnApplication(long projectId,  long organisationId) {
        return getProject(projectId).andOnSuccessReturn(p -> p.getApplication().getApplicantProcessRoles().stream()
                .anyMatch(pr -> pr.getOrganisationId() == organisationId));
    }

    @Override
    @Transactional
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        List<ServiceResult<ProjectResource>> projectCreationResults = applicationFundingDecisions
                .keySet()
                .stream()
                .filter(d -> applicationFundingDecisions.get(d).equals(FundingDecision.FUNDED))
                .map(this::createSingletonProjectFromApplicationId)
                .collect(toList());

        boolean anyProjectCreationFailed = simpleAnyMatch(projectCreationResults, BaseFailingOrSucceedingResult::isFailure);

        return anyProjectCreationFailed ?
                serviceFailure(CREATE_PROJECT_FROM_APPLICATION_FAILS) : serviceSuccess();
    }

    @Override
    @Transactional (readOnly = true)
    public ServiceResult<List<ProjectResource>> findByUserId(long userId) {
        List<ProjectUser> projectUsers = projectUserRepository.findByUserId(userId);
        return serviceSuccess(projectUsers
                .stream()
                .map(ProjectUser::getProcess)
                .distinct()
                .map(projectMapper::mapToResource)
                .collect(toList())
        );
    }

    @Override
    public ServiceResult<List<ProjectUserResource>> getProjectUsersByProjectIdAndRoleIn(long projectId, List<ProjectParticipantRole> projectParticipantRoles) {
        return serviceSuccess(simpleMap(getProjectUsersByProjectId(projectId, projectParticipantRoles), projectUserMapper::mapToResource));
    }

    @Override
    @Transactional
    public ServiceResult<ProjectUser> addPartner(long projectId, long userId, long organisationId) {
        return find(getProject(projectId), getOrganisation(organisationId), getUser(userId)).
                andOnSuccess((project, organisation, user) -> {
                    if (project.getOrganisations(o -> o.getId() == organisationId).isEmpty()) {
                        return serviceFailure(badRequestError("project does not contain organisation"));
                    }
                    return addProjectPartner(project, user, organisation);
                });
    }

    private ServiceResult<ProjectUser> addProjectPartner(Project project, User user, Organisation organisation) {
        List<ProjectUser> partners = project.getProjectUsersWithRole(PROJECT_PARTNER);
        Optional<ProjectUser> projectUser = simpleFindFirst(partners, p -> p.getUser().getId().equals(user.getId()));
        if (projectUser.isPresent()) {
            return serviceSuccess(projectUser.get()); // Already a partner
        } else {
            ProjectUser pu = new ProjectUser(user, project, PROJECT_PARTNER, organisation);
            return serviceSuccess(pu);
        }
    }

    @Override
    public ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(long projectId, long userId) {
        ProjectUser projectUser = projectUserRepository.findFirstByProjectIdAndUserIdAndRoleIsIn(projectId, userId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()));
        if (projectUser != null && projectUser.getOrganisation() != null) {
            return serviceSuccess(organisationMapper.mapToResource(organisationRepository.findById(projectUser.getOrganisation().getId()).orElse(null)));
        } else {
            return serviceFailure(new Error(CANNOT_FIND_ORG_FOR_GIVEN_PROJECT_AND_USER, NOT_FOUND));
        }
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }

    @Override
    @Transactional
    public ServiceResult<ProjectResource> createProjectFromApplication(long applicationId) {

        return getApplication(applicationId).andOnSuccess(application -> {

            if (FundingDecisionStatus.FUNDED.equals(application.getFundingDecision())) {
                return createSingletonProjectFromApplicationId(applicationId);
            } else {
                return serviceFailure(CREATE_PROJECT_FROM_APPLICATION_FAILS);
            }
        });
    }

    @Override
    public ServiceResult<OrganisationResource> getLeadOrganisation(long projectId) {
        return getProject(projectId)
                .andOnSuccessReturn(p -> p.getApplication().getLeadOrganisationId())
                .andOnSuccess(this::getOrganisation)
                .andOnSuccessReturn(o -> organisationMapper.mapToResource(o));
    }

    private ServiceResult<ProjectResource> createSingletonProjectFromApplicationId(long applicationId) {
        Optional<ProjectResource> existingProject = getByApplicationId(applicationId).getOptionalSuccessObject();
        if (existingProject.isPresent()) {
            return serviceSuccess(existingProject.get());
        }
        return createProjectFromApplicationId(applicationId).andOnSuccessReturn(project -> {
            activityLogService.recordActivityByApplicationId(applicationId, ActivityType.APPLICATION_INTO_PROJECT_SETUP);
            return project;
        });
    }

    private ServiceResult<ProjectResource> createProjectFromApplicationId(long applicationId) {
        return getApplication(applicationId).andOnSuccess(application -> {

            Project project = new Project();
            project.setApplication(application);
            project.setDurationInMonths(application.getDurationInMonths());
            project.setName(application.getName());
            project.setTargetStartDate(application.getStartDate());

            ProcessRole leadApplicantRole = simpleFindFirst(application.getProcessRoles(), ProcessRole::isLeadApplicant).get();
            List<ProcessRole> collaborativeRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isCollaborator);
            List<ProcessRole> allRoles = combineLists(leadApplicantRole, collaborativeRoles);

            List<ServiceResult<ProjectUser>> correspondingProjectUsers = simpleMap(allRoles,
                    role -> {
                        Organisation organisation = organisationRepository.findById(role.getOrganisationId()).orElse(null);
                        return createPartnerProjectUser(project, role.getUser(), organisation);
                    });

            ServiceResult<List<ProjectUser>> projectUserCollection = aggregate(correspondingProjectUsers);

            ServiceResult<Project> saveProjectResult = projectUserCollection.andOnSuccessReturn(projectUsers -> {

                List<Organisation> uniqueOrganisations =
                        removeDuplicates(simpleMap(projectUsers, ProjectUser::getOrganisation));

                List<PartnerOrganisation> partnerOrganisations = simpleMap(uniqueOrganisations, org ->
                        createPartnerOrganisation(application, project, org, leadApplicantRole));

                project.setProjectUsers(projectUsers);
                project.setPartnerOrganisations(partnerOrganisations);
                return projectRepository.save(project);
            });

            return saveProjectResult.
                    andOnSuccess(newProject -> createProcessEntriesForNewProject(newProject).
                            andOnSuccess(() -> generateFinanceCheckEntitiesForNewProject(newProject)).
                            andOnSuccess(() -> setCompetitionProjectSetupStartedDate(newProject)).
                            andOnSuccessReturn(() -> projectMapper.mapToResource(newProject)));
        });
    }

    private void setCompetitionProjectSetupStartedDate(Project newProject) {
        Competition competition = newProject.getApplication().getCompetition();
        if (competition.getProjectSetupStarted() == null) {
            competition.setProjectSetupStarted(ZonedDateTime.now());
        }
    }

    private PartnerOrganisation createPartnerOrganisation(Application application, Project project, Organisation org, ProcessRole leadApplicantRole) {
        PartnerOrganisation partnerOrganisation = new PartnerOrganisation(project, org, org.getId().equals(leadApplicantRole.getOrganisationId()));

        simpleFindFirst(application.getApplicationFinances(), applicationFinance -> applicationFinance.getOrganisation().getId().equals(org.getId()))
                .ifPresent(applicationFinance -> partnerOrganisation.setPostcode(applicationFinance.getWorkPostcode()));

        return partnerOrganisation;
    }

    private ServiceResult<ProjectUser> createPartnerProjectUser(Project project, User user, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PROJECT_PARTNER);
    }

    private ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, ProjectParticipantRole role) {
        return serviceSuccess(new ProjectUser(user, project, role, organisation));
    }

    private ServiceResult<Void> createProcessEntriesForNewProject(Project newProject) {

        ProjectUser originalLeadApplicantProjectUser = newProject.getProjectUsers().get(0);

        ServiceResult<Void> projectDetailsProcess = createProjectDetailsProcess(newProject, originalLeadApplicantProjectUser);
        ServiceResult<Void> viabilityProcesses = createViabilityProcesses(newProject.getPartnerOrganisations(), originalLeadApplicantProjectUser);
        ServiceResult<Void> eligibilityProcesses = createEligibilityProcesses(newProject.getPartnerOrganisations(), originalLeadApplicantProjectUser);
        ServiceResult<Void> golProcess = createGOLProcess(newProject, originalLeadApplicantProjectUser);
        ServiceResult<Void> projectProcess = createProjectProcess(newProject, originalLeadApplicantProjectUser);
        ServiceResult<Void> spendProfileProcess = createSpendProfileProcess(newProject, originalLeadApplicantProjectUser);

        projectRepository.refresh(newProject);
        
        return processAnyFailuresOrSucceed(projectDetailsProcess, viabilityProcesses, eligibilityProcesses, golProcess, projectProcess, spendProfileProcess);
    }

    private ServiceResult<Void> createProjectDetailsProcess(Project newProject, ProjectUser originalLeadApplicantProjectUser) {
        if (projectDetailsWorkflowHandler.projectCreated(newProject, originalLeadApplicantProjectUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES);
        }
    }

    private ServiceResult<Void> createViabilityProcesses(List<PartnerOrganisation> partnerOrganisations, ProjectUser originalLeadApplicantProjectUser) {

        List<ServiceResult<Void>> results = simpleMap(partnerOrganisations, partnerOrganisation ->
                viabilityWorkflowHandler.projectCreated(partnerOrganisation, originalLeadApplicantProjectUser) ?
                        serviceSuccess() :
                        serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES));

        return aggregate(results).andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> createEligibilityProcesses(List<PartnerOrganisation> partnerOrganisations, ProjectUser originalLeadApplicantProjectUser) {

        List<ServiceResult<Void>> results = simpleMap(partnerOrganisations, partnerOrganisation ->
                eligibilityWorkflowHandler.projectCreated(partnerOrganisation, originalLeadApplicantProjectUser) ?
                        serviceSuccess() :
                        serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES));

        return aggregate(results).andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> createGOLProcess(Project newProject, ProjectUser originalLeadApplicantProjectUser) {
        if (golWorkflowHandler.projectCreated(newProject, originalLeadApplicantProjectUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES);
        }
    }

    private ServiceResult<Void> createProjectProcess(Project newProject, ProjectUser originalLeadApplicantProjectUser) {
        if (projectWorkflowHandler.projectCreated(newProject, originalLeadApplicantProjectUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES);
        }
    }

    private ServiceResult<Void> createSpendProfileProcess(Project newProject, ProjectUser originalLeadApplicantProjectUser) {
        if (spendProfileWorkflowHandler.projectCreated(newProject, originalLeadApplicantProjectUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES);
        }
    }

    private ServiceResult<Void> generateFinanceCheckEntitiesForNewProject(Project newProject) {
        List<Organisation> organisations = newProject.getOrganisations();

        List<ServiceResult<Void>> financeCheckResults = simpleMap(organisations, organisation ->
                financeChecksGenerator.createFinanceChecksFigures(newProject, organisation).andOnSuccess(() ->
                        costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(newProject.getId(), organisation.getId()).andOnSuccess(costCategoryType ->
                                financeChecksGenerator.createMvpFinanceChecksFigures(newProject, organisation, costCategoryType))));

        return processAnyFailuresOrSucceed(financeCheckResults);
    }

    private ServiceResult<Project> getProjectByApplication(long applicationId) {
        return find(projectRepository.findOneByApplicationId(applicationId), notFoundError(Project.class, applicationId));
    }

    private List<ProjectUser> getProjectUsersByProjectId(Long projectId, List<ProjectParticipantRole> projectParticipantRoles) {
        return projectUserRepository.findByProjectIdAndRoleIsIn(projectId, projectParticipantRoles);
    }
}