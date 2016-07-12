package com.worth.ifs.project.transactional;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.repository.AddressTypeRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.mapper.MonitoringOfficerMapper;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.MonitoringOfficerRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Service
public class ProjectServiceImpl extends BaseTransactionalService implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private MonitoringOfficerMapper monitoringOfficerMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        MONITORING_OFFICER_ASSIGNED,
    }

    @Override
    public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProjectResource> getByApplicationId(@P("applicationId") Long applicationId) {
        return getProjectByApplication(applicationId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return createProjectFromApplicationId(applicationId);
    }

    @Override
    public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
        return getProject(projectId).
                andOnSuccess(project -> validateProjectManager(project, projectManagerId).
                        andOnSuccessReturnVoid(project::setProjectManager));
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId).
                        andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate)));
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
        return getProject(projectId).
                andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, organisationId, financeContactUserId).
                        andOnSuccess(projectUser -> createFinanceContactProjectUser(projectUser.getUser(), project, projectUser.getOrganisation()).
                                andOnSuccessReturnVoid(financeContact -> addFinanceContactToProject(project, financeContact))));
    }

    @Override
    public ServiceResult<Void> updateProjectAddress(Long organisationId, Long projectId, OrganisationAddressType organisationAddressType, AddressResource address) {
        Project project = projectRepository.findOne(projectId);
        Organisation leadOrganisation = organisationRepository.findOne(organisationId);
        if (address.getId() != null && addressRepository.exists(address.getId())) {
            Address existingAddress = addressRepository.findOne(address.getId());
            project.setAddress(existingAddress);
        } else {
            Address newAddress = addressMapper.mapToDomain(address);
            if(address.getOrganisations() == null || address.getOrganisations().size() == 0){
                AddressType addressType = addressTypeRepository.findOne((long)organisationAddressType.getOrdinal());
                List<OrganisationAddress> existingOrgAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(leadOrganisation.getId(), addressType);
                existingOrgAddresses.stream().forEach(oA -> organisationAddressRepository.delete(oA));
                OrganisationAddress organisationAddress = new OrganisationAddress(leadOrganisation, newAddress, addressType);
                organisationAddressRepository.save(organisationAddress);
            }
            project.setAddress(newAddress);
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        applicationFundingDecisions.keySet().stream().filter(d -> applicationFundingDecisions.get(d).equals(FundingDecision.FUNDED)).forEach(this::createProjectFromApplicationId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUserId(final Long userId) {
        List<ProjectUser> projectUsers = projectUserRepository.findByUserId(userId);
        List<Project> projects = simpleMap(projectUsers, ProjectUser::getProject).parallelStream().distinct().collect(Collectors.toList());     //Users may have multiple roles (e.g. partner and finance contact, in which case there will be multiple project_user entries, so this is flatting it).
        return serviceSuccess(simpleMap(projects, projectMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
        List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectId);
        return serviceSuccess(simpleMap(projectUsers, projectUserMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> saveProjectSubmitDateTime(final Long projectId, LocalDateTime date) {
        return getProject(projectId).
                andOnSuccess(
                        project -> {
                            if(validateIsReadyForSubmission(project)){
                                return setSubmittedDate(project, date);
                            } else {
                                return serviceFailure(new Error(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE));
                            }
                        }
                ).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> serviceSuccess(validateIsReadyForSubmission(project)));
    }

    @Override
    public ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId) {
        return getExistingMonitoringOfficerForProject(projectId).andOnSuccessReturn(monitoringOfficerMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> saveMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        return validateMonitoringOfficer(projectId, monitoringOfficerResource).
            andOnSuccess(() -> validateInMonitoringOfficerAssignableState(projectId)).
            andOnSuccess(() -> saveMonitoringOfficer(monitoringOfficerResource));
    }

    @Override
    public ServiceResult<Void> notifyMonitoringOfficer(MonitoringOfficerResource monitoringOfficer) {

        Notification notification = createMonitoringOfficerAssignedNotification(monitoringOfficer);

        ServiceResult<Void> moAssignedEmailSendResult = notificationService.sendNotification(notification, EMAIL);

        return processAnyFailuresOrSucceed(asList(moAssignedEmailSendResult));
    }


    private Notification createMonitoringOfficerAssignedNotification(MonitoringOfficerResource monitoringOfficer) {

        NotificationTarget notificationTarget = createMonitoringOfficerAssignedNotificationTarget(monitoringOfficer);

        Map<String, Object> globalArguments = createGlobalArgsForMonitoringOfficerAssignedEmail(monitoringOfficer);

        return new Notification(systemNotificationSource, singletonList(notificationTarget),
                Notifications.MONITORING_OFFICER_ASSIGNED, globalArguments, Collections.EMPTY_MAP);

    }

    private NotificationTarget createMonitoringOfficerAssignedNotificationTarget(MonitoringOfficerResource monitoringOfficer) {

        String fullName = getMonitoringOfficerFullName(monitoringOfficer);

        return new ExternalUserNotificationTarget(fullName, monitoringOfficer.getEmail());

    }
    private String getMonitoringOfficerFullName(MonitoringOfficerResource monitoringOfficer) {

        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return monitoringOfficer.getFirstName() + " " + monitoringOfficer.getLastName();
    }

    private Map<String, Object> createGlobalArgsForMonitoringOfficerAssignedEmail(MonitoringOfficerResource monitoringOfficer) {

        Project project = projectRepository.findOne(monitoringOfficer.getProject());
        User projectManager = project.getProjectManager().getUser();

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("dashboardUrl", webBaseUrl);
        globalArguments.put("projectName", project.getName());
        globalArguments.put("leadOrganisation", project.getApplication().getLeadOrganisation().getName());
        globalArguments.put("projectManagerName", projectManager.getFirstName() + " " + projectManager.getLastName());
        globalArguments.put("projectManagerEmail", projectManager.getEmail());

        return globalArguments;

    }

    private ServiceResult<Void> validateMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        if (!projectId.equals(monitoringOfficerResource.getProject())) {
            return serviceFailure(new Error(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE));
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<Void> validateInMonitoringOfficerAssignableState(final Long projectId) {

        return getProject(projectId).andOnSuccess(project -> {
           if (!project.isProjectDetailsSubmitted()) {
               return serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED));
           }
            return serviceSuccess();
        });
    }

    private ServiceResult<Void> saveMonitoringOfficer(final MonitoringOfficerResource monitoringOfficerResource) {

        return getExistingMonitoringOfficerForProject(monitoringOfficerResource.getProject()).handleSuccessOrFailure(
            noMonitoringOfficer -> saveNewMonitoringOfficer(monitoringOfficerResource),
            existingMonitoringOfficer -> updateExistingMonitoringOfficer(existingMonitoringOfficer, monitoringOfficerResource)
        );
    }

    private ServiceResult<Void> updateExistingMonitoringOfficer(MonitoringOfficer existingMonitoringOfficer, MonitoringOfficerResource updateDetails) {
        existingMonitoringOfficer.setFirstName(updateDetails.getFirstName());
        existingMonitoringOfficer.setLastName(updateDetails.getLastName());
        existingMonitoringOfficer.setEmail(updateDetails.getEmail());
        existingMonitoringOfficer.setPhoneNumber(updateDetails.getPhoneNumber());
        return serviceSuccess();
    }

    private ServiceResult<Void> saveNewMonitoringOfficer(MonitoringOfficerResource monitoringOfficerResource) {
        MonitoringOfficer monitoringOfficer = monitoringOfficerMapper.mapToDomain(monitoringOfficerResource);
        monitoringOfficerRepository.save(monitoringOfficer);
        return serviceSuccess();
    }

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    private ServiceResult<Void> setSubmittedDate(Project project, LocalDateTime date) {
        project.setSubmittedDate(date);
        return serviceSuccess();
    }

    private void addFinanceContactToProject(Project project, ProjectUser financeContact) {

        ProjectUser existingUser = project.getExistingProjectUserWithRoleForOrganisation(FINANCE_CONTACT, financeContact.getOrganisation());

        if (existingUser != null) {
            project.removeProjectUser(existingUser);
        }

        project.addProjectUser(financeContact);
    }

    private ServiceResult<ProjectUser> createFinanceContactProjectUser(User user, Project project, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, FINANCE_CONTACT);
    }

    private ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));
        }

        return serviceSuccess();
    }

    private ServiceResult<ProjectUser> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

        List<ProjectUser> projectUsers = project.getProjectUsers();

        List<ProjectUser> matchingUserOrganisationProcessRoles = simpleFilter(projectUsers,
                pr -> organisationId.equals(pr.getOrganisation().getId()) && financeContactUserId.equals(pr.getUser().getId()));

        if (matchingUserOrganisationProcessRoles.isEmpty()) {
            return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
        }

        List<ProjectUser> partnerUsers = simpleFilter(matchingUserOrganisationProcessRoles, ProjectUser::isPartner);

        if (partnerUsers.isEmpty()) {
            return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
        }

        return getOnlyElementOrFail(partnerUsers);
    }

    private ServiceResult<ProcessRole> validateProjectManager(Project project, Long projectManagerId) {
        Application application = applicationRepository.findOne(project.getApplication().getId());
        Organisation leadPartner = application.getLeadOrganisation();

        List<ProcessRole> leadPartnerProcessRoles = simpleFilter(application.getProcessRoles(), pr -> leadPartner.equals(pr.getOrganisation()));
        List<ProcessRole> matchingProcessRoles = simpleFilter(leadPartnerProcessRoles, lppr -> projectManagerId.equals(lppr.getUser().getId()));

        if(!matchingProcessRoles.isEmpty()) {
            return getOnlyElementOrFail(matchingProcessRoles).andOnSuccess(processRole -> serviceSuccess(processRole));
        } else {
            return serviceFailure(new Error(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION));
        }
    }

    private ServiceResult<ProjectResource> createProjectFromApplicationId(final Long applicationId){
        return getApplication(applicationId).andOnSuccess(application -> {
            Project project = new Project();
            project.setApplication(application);
            project.setDurationInMonths(application.getDurationInMonths());
            project.setName(application.getName());
            project.setTargetStartDate(application.getStartDate());

            List<ProcessRole> collaborativeRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
            List<ServiceResult<ProjectUser>> correspondingProjectUsers = simpleMap(collaborativeRoles, role -> createPartnerProjectUser(project, role.getUser(), role.getOrganisation()));
            ServiceResult<List<ProjectUser>> projectUserCollection = aggregate(correspondingProjectUsers);

            return projectUserCollection.andOnSuccessReturn(projectUsers -> {
                projectUsers.forEach(project::addProjectUser);
                Project createdProject = projectRepository.save(project);
                return projectMapper.mapToResource(createdProject);
            });
        });
    }

    private ServiceResult<ProjectUser> createPartnerProjectUser(Project project, User user, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PARTNER);
    }

    private ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, UserRoleType roleType) {
        return getRole(roleType).andOnSuccessReturn(role -> new ProjectUser(user, project, role, organisation));
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }

    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<Address> getAddress(long addressId) {
        return find(addressRepository.findOne(addressId), notFoundError(Address.class, addressId));
    }

    private ServiceResult<Project> getProjectByApplication(long applicationId){
        return find(projectRepository.findOneByApplicationId(applicationId), notFoundError(Project.class, applicationId));
    }

    private boolean validateIsReadyForSubmission(final Project project) {
        return !(project.getAddress() == null || project.getProjectManager() == null || project.getTargetStartDate() == null || allFinanceContactsNotSet(project.getId()) || project.getSubmittedDate() != null);
    }

    private boolean allFinanceContactsNotSet(Long projectId){
        List<ProjectUser> projectUserObjs = projectUserRepository.findByProjectId(projectId);
        List<ProjectUserResource> projectUserResources = simpleMap(projectUserObjs, projectUserMapper::mapToResource);
        List<Organisation> partnerOrganisations = getPartnerOrganisations(projectUserResources);
        List<ProjectUserResource> financeRoles = simpleFilter(projectUserResources, ProjectUserResource::isFinanceContact);
        return financeRoles.size() < partnerOrganisations.size();
    }

    private List<Organisation> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);

        final Supplier<SortedSet<Organisation>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<Organisation> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PARTNER.getName()))
                .map(uar -> organisationRepository.findOne(uar.getOrganisation()))
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }
}