package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_FIND_ORG_FOR_GIVEN_PROJECT_AND_USER;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CREATE_PROJECT_FROM_APPLICATION_FAILS;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProjectServiceImpl extends AbstractProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

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

    @Override
    public ServiceResult<ProjectResource> getProjectById(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProjectResource> getByApplicationId(Long applicationId) {
        return getProjectByApplication(applicationId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        List<ServiceResult<ProjectResource>> projectCreationResults = applicationFundingDecisions.keySet().stream().filter(d -> applicationFundingDecisions.get(d).equals(FundingDecision.FUNDED)).map(this::createSingletonProjectFromApplicationId).collect(toList());
        long failedProjectCreationCount = projectCreationResults.stream().filter(r -> r.isFailure()).count();
        if (failedProjectCreationCount > 0) {
            return serviceFailure(CREATE_PROJECT_FROM_APPLICATION_FAILS);
        } else {
            return serviceSuccess();
        }
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUserId(final Long userId) {
        List<ProjectUser> projectUsers = projectUserRepository.findByUserId(userId);
        List<Project> projects = simpleMap(projectUsers, ProjectUser::getProcess).parallelStream().distinct().collect(toList());     //Users may have multiple roles (e.g. partner and finance contact, in which case there will be multiple project_user entries, so this is flatting it).
        return serviceSuccess(simpleMap(projects, projectMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
        return serviceSuccess(simpleMap(getProjectUsersByProjectId(projectId), projectUserMapper::mapToResource));
    }

    @Override
    @Transactional
    public ServiceResult<ProjectUser> addPartner(Long projectId, Long userId, Long organisationId) {
        return find(getProject(projectId), getOrganisation(organisationId), getUser(userId)).
                andOnSuccess((project, organisation, user) -> {
                    if (project.getOrganisations(o -> organisationId.equals(o.getId())).isEmpty()) {
                        return serviceFailure(badRequestError("project does not contain organisation"));
                    }
                    addProcessRoles(project, user, organisation);
                    return addProjectPartner(project, user, organisation);
                });
    }

    private ServiceResult<ProjectUser> addProjectPartner(Project project, User user, Organisation organisation){
        List<ProjectUser> partners = project.getProjectUsersWithRole(PROJECT_PARTNER);
        Optional<ProjectUser> projectUser = simpleFindFirst(partners, p -> p.getUser().getId().equals(user.getId()));
        if (projectUser.isPresent()) {
            return serviceSuccess(projectUser.get()); // Already a partner
        } else {
            ProjectUser pu = new ProjectUser(user, project, PROJECT_PARTNER, organisation);
            return serviceSuccess(pu);
        }
    }

    private void addProcessRoles(Project project, User user, Organisation organisation) {
        Application application = project.getApplication();
        Role role = roleRepository.findOneByName(COLLABORATOR.getName());
        ProcessRole processRole = new ProcessRole(user, application.getId(), role, organisation.getId());
        processRoleRepository.save(processRole);
    }

    @Override
    public ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId) {
        ProjectUser projectUser = projectUserRepository.findByProjectIdAndRoleAndUserId(projectId, PROJECT_PARTNER, userId);
        if (projectUser != null && projectUser.getOrganisation() != null) {
            return serviceSuccess(organisationMapper.mapToResource(organisationRepository.findOne(projectUser.getOrganisation().getId())));
        } else {
            return serviceFailure(new Error(CANNOT_FIND_ORG_FOR_GIVEN_PROJECT_AND_USER, NOT_FOUND));
        }
    }

    @Override
    public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
        return find(projectUserRepository.findByProjectIdAndRole(projectId, ProjectParticipantRole.PROJECT_MANAGER),
                notFoundError(ProjectUserResource.class, projectId)).andOnSuccessReturn(projectUserMapper::mapToResource);
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
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return createSingletonProjectFromApplicationId(applicationId);
    }

    private ServiceResult<ProjectResource> createSingletonProjectFromApplicationId(final Long applicationId) {

        return checkForExistingProjectWithApplicationId(applicationId).handleSuccessOrFailure(
                failure -> createProjectFromApplicationId(applicationId),
                success -> serviceSuccess(success)
        );
    }

    private ServiceResult<ProjectResource> checkForExistingProjectWithApplicationId(Long applicationId) {
        return getByApplicationId(applicationId);
    }

    private ServiceResult<ProjectResource> createProjectFromApplicationId(final Long applicationId) {

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
                        Organisation organisation = organisationRepository.findOne(role.getOrganisationId());
                        return createPartnerProjectUser(project, role.getUser(), organisation);
                    });

            ServiceResult<List<ProjectUser>> projectUserCollection = aggregate(correspondingProjectUsers);

            ServiceResult<Project> saveProjectResult = projectUserCollection.andOnSuccessReturn(projectUsers -> {

                List<Organisation> uniqueOrganisations =
                        removeDuplicates(simpleMap(projectUsers, ProjectUser::getOrganisation));

                List<PartnerOrganisation> partnerOrganisations = simpleMap(uniqueOrganisations, org ->
                        new PartnerOrganisation(project, org, org.getId().equals(leadApplicantRole.getOrganisationId())));

                project.setProjectUsers(projectUsers);
                project.setPartnerOrganisations(partnerOrganisations);
                return projectRepository.save(project);
            });

            return saveProjectResult.
                    andOnSuccess(newProject -> createProcessEntriesForNewProject(newProject).
                            andOnSuccess(() -> generateFinanceCheckEntitiesForNewProject(newProject)).
                            andOnSuccessReturn(() -> projectMapper.mapToResource(newProject)));
        });
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

        return processAnyFailuresOrSucceed(projectDetailsProcess, viabilityProcesses, eligibilityProcesses, golProcess, projectProcess);
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
}
