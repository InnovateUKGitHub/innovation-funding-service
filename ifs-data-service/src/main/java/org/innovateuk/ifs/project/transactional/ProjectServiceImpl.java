package org.innovateuk.ifs.project.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.invite.mapper.InviteProjectMapper;
import org.innovateuk.ifs.invite.repository.InviteProjectRepository;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.SpendProfile;
import org.innovateuk.ifs.project.financecheck.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.financecheck.transactional.CostCategoryTypeStrategy;
import org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import org.innovateuk.ifs.project.mapper.MonitoringOfficerMapper;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_REQUIRED;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProjectServiceImpl extends AbstractProjectServiceImpl implements ProjectService {
    private static final Log LOG = LogFactory.getLog(ProjectServiceImpl.class);

    public static final String WEB_CONTEXT = "/project-setup";
    private static final String GOL_STATE_ERROR = "Set Grant Offer Letter workflow status to sent failed for project %s";
    private static final String PROJECT_STATE_ERROR = "Set project status to live failed for project %s";

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
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private  EmailService projectEmailService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private InviteProjectRepository inviteProjectRepository;

    @Autowired
    private InviteProjectMapper inviteProjectMapper;

    @Autowired
    private FinanceChecksGenerator financeChecksGenerator;

    @Autowired
    private ProjectGrantOfferService projectGrantOfferLetterService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        MONITORING_OFFICER_ASSIGNED,
        MONITORING_OFFICER_ASSIGNED_PROJECT_MANAGER,
        INVITE_FINANCE_CONTACT,
        INVITE_PROJECT_MANAGER,
        GRANT_OFFER_LETTER_PROJECT_MANAGER,
        PROJECT_LIVE
    }

    @Override
    public ServiceResult<ProjectResource> getProjectById(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProjectResource> getByApplicationId(Long applicationId) {
        return getProjectByApplication(applicationId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return createSingletonProjectFromApplicationId(applicationId);
    }

    @Override
    public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerUserId) {
        return getProject(projectId).
                andOnSuccess(this::validateIfProjectAlreadySubmitted).
                andOnSuccess(project -> validateProjectManager(project, projectManagerUserId).
                        andOnSuccess(leadPartner -> createOrUpdateProjectManagerForProject(project, leadPartner)));
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId)).
                andOnSuccess(this::validateIfProjectAlreadySubmitted).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate));
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return getProject(composite.getProjectId()).
                andOnSuccess(this::validateProjectIsInSetup).
                    andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, composite.getOrganisationId(), financeContactUserId).
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
            if (address.getOrganisations() == null || address.getOrganisations().size() == 0) {
                AddressType addressType = addressTypeRepository.findOne(organisationAddressType.getOrdinal());
                List<OrganisationAddress> existingOrgAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(leadOrganisation.getId(), addressType);
                existingOrgAddresses.forEach(oA -> organisationAddressRepository.delete(oA));
                OrganisationAddress organisationAddress = new OrganisationAddress(leadOrganisation, newAddress, addressType);
                organisationAddressRepository.save(organisationAddress);
            }
            project.setAddress(newAddress);
        }


        return getCurrentlyLoggedInPartner(project).andOnSuccessReturn(user ->
                projectDetailsWorkflowHandler.projectAddressAdded(project, user)).andOnSuccess(workflowResult ->
                workflowResult ? serviceSuccess() : serviceFailure(PROJECT_SETUP_CANNOT_PROGRESS_WORKFLOW));
    }

    @Override
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        applicationFundingDecisions.keySet().stream().filter(d -> applicationFundingDecisions.get(d).equals(FundingDecision.FUNDED)).forEach(this::createSingletonProjectFromApplicationId);
        return serviceSuccess();
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
    public ServiceResult<Void> submitProjectDetails(final Long projectId, ZonedDateTime date) {

        return getProject(projectId).andOnSuccess(project ->
                getCurrentlyLoggedInPartner(project).andOnSuccess(projectUser -> {

                    if (projectDetailsWorkflowHandler.submitProjectDetails(project, projectUser)) {
                        return serviceSuccess();
                    } else {
                        return serviceFailure(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE);
                    }
                }));
    }

    @Override
    public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(this::doIsSubmissionAllowed);
    }

    private boolean doIsSubmissionAllowed(Project project) {
        return projectDetailsWorkflowHandler.isSubmissionAllowed(project);
    }

    @Override
    public ServiceResult<Void> saveDocumentsSubmitDateTime(Long projectId, ZonedDateTime date) {

        return getProject(projectId).andOnSuccess(project ->
                retrieveUploadedDocuments(projectId).handleSuccessOrFailure(
                        failure -> serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_MUST_BE_UPLOADED_BEFORE_SUBMIT),
                        success -> setDocumentsSubmittedDate(project, date)));
    }

    private ServiceResult<Void> setDocumentsSubmittedDate(Project project, ZonedDateTime date) {
        project.setDocumentsSubmittedDate(date);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId) {

        ServiceResult<Project> project = getProject(projectId);
        Optional<ProjectUser> projectManager = getExistingProjectManager(project.getSuccessObject());

        return retrieveUploadedDocuments(projectId).handleSuccessOrFailure(
                failure -> serviceSuccess(false),
                success -> projectManager.isPresent() && projectManager.get().getUser().getId().equals(userId) && project.getSuccessObject().getDocumentsSubmittedDate() == null ?
                        serviceSuccess(true) :
                        serviceSuccess(false));
    }

    @Override
    public ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId) {
        return getExistingMonitoringOfficerForProject(projectId).andOnSuccessReturn(monitoringOfficerMapper::mapToResource);
    }

    @Override
    public ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        return validateMonitoringOfficer(projectId, monitoringOfficerResource).
                andOnSuccess(() -> validateInMonitoringOfficerAssignableState(projectId)).
                andOnSuccess(() -> saveMonitoringOfficer(monitoringOfficerResource));
    }

    @Override
    public ServiceResult<Void> notifyStakeholdersOfMonitoringOfficerChange(MonitoringOfficerResource monitoringOfficer) {

        Project project = projectRepository.findOne(monitoringOfficer.getProject());
        User projectManager = getExistingProjectManager(project).get().getUser();

        NotificationTarget moTarget = createMonitoringOfficerNotificationTarget(monitoringOfficer);
        NotificationTarget pmTarget = createProjectManagerNotificationTarget(projectManager);

        ServiceResult<Void> moAssignedEmailSendResult = projectEmailService.sendEmail(singletonList(moTarget), createGlobalArgsForMonitoringOfficerAssignedEmail(monitoringOfficer, project, projectManager), ProjectServiceImpl.Notifications.MONITORING_OFFICER_ASSIGNED);
        ServiceResult<Void> pmAssignedEmailSendResult = projectEmailService.sendEmail(singletonList(pmTarget), createGlobalArgsForMonitoringOfficerAssignedEmail(monitoringOfficer, project, projectManager), ProjectServiceImpl.Notifications.MONITORING_OFFICER_ASSIGNED_PROJECT_MANAGER);

        return processAnyFailuresOrSucceed(asList(moAssignedEmailSendResult, pmAssignedEmailSendResult));
    }

    @Override
    public ServiceResult<FileEntryResource> createCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkCollaborationAgreementFileToProject(project, fileDetails)));
    }

    @Override
    public ServiceResult<FileAndContents> getCollaborationAgreementFileContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getCollaborationAgreement();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
            return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getCollaborationAgreement();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<Void> updateCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkCollaborationAgreementFileToProject(project, fileDetails)));
    }

    @Override
    public ServiceResult<Void> deleteCollaborationAgreementFile(Long projectId) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project ->
                getCollaborationAgreement(project).andOnSuccess(fileEntry ->
                        fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                removeCollaborationAgreementFileFromProject(project))));
    }

    @Override
    public ServiceResult<FileEntryResource> createExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkExploitationPlanFileToProject(project, fileDetails)));
    }

    @Override
    public ServiceResult<FileAndContents> getExploitationPlanFileContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getExploitationPlan();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
            return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getExploitationPlanFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getExploitationPlan();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<Void> updateExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkExploitationPlanFileToProject(project, fileDetails)));
    }

    @Override
    public ServiceResult<Void> deleteExploitationPlanFile(Long projectId) {
        return getProject(projectId).
        andOnSuccess(this::validateProjectIsInSetup).andOnSuccess(project ->
                getExploitationPlan(project).andOnSuccess(fileEntry ->
                        fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                removeExploitationPlanFileFromProject(project))));
    }

    @Override
    public ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approval) {
        //TODO INFUND-7493
        if (approval == null) {
            return serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_APPROVAL_DECISION_MUST_BE_PROVIDED);
        }
        return getProject(projectId)
                .andOnSuccess(project -> {
                    if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved())) {
                        return serviceFailure(PROJECT_SETUP_OTHER_DOCUMENTS_HAVE_ALREADY_BEEN_APPROVED);
                    }
                    project.setOtherDocumentsApproved(approval ? ApprovalType.APPROVED : ApprovalType.REJECTED);
                    if (approval.equals(false)) {
                        project.setDocumentsSubmittedDate(null);
                    }
                    return projectGrantOfferLetterService.generateGrantOfferLetterIfReady(projectId).andOnFailure(() -> serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));
                });
    }

    private ServiceResult<List<FileEntryResource>> retrieveUploadedDocuments(Long projectId) {

        ServiceResult<FileEntryResource> collaborationAgreementFile = getCollaborationAgreementFileEntryDetails(projectId);
        ServiceResult<FileEntryResource> exploitationPlanFile = getExploitationPlanFileEntryDetails(projectId);

        return aggregate(asList(collaborationAgreementFile, exploitationPlanFile));
    }

    @Override
    public ServiceResult<ProjectUser> addPartner(Long projectId, Long userId, Long organisationId) {
        return find(getProject(projectId), getOrganisation(organisationId), getUser(userId)).
                andOnSuccess((project, organisation, user) -> {
                    if (project.getOrganisations(o -> organisationId.equals(o.getId())).isEmpty()) {
                        return serviceFailure(badRequestError("project does not contain organisation"));
                    }
                    List<ProjectUser> partners = project.getProjectUsersWithRole(PROJECT_PARTNER);
                    Optional<ProjectUser> projectUser = simpleFindFirst(partners, p -> p.getUser().getId().equals(userId));
                    if (projectUser.isPresent()) {
                        return serviceSuccess(projectUser.get()); // Already a partner
                    } else {
                        ProjectUser pu = new ProjectUser(user, project, PROJECT_PARTNER, organisation);
                        return serviceSuccess(pu);
                    }
                });
    }

    private ServiceResult<FileEntry> getCollaborationAgreement(Project project) {
        if (project.getCollaborationAgreement() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getCollaborationAgreement());
        }
    }

    private FileEntryResource linkCollaborationAgreementFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        linkCollaborationAgreementFileEntryToProject(fileEntry, project);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private void linkCollaborationAgreementFileEntryToProject(FileEntry fileEntry, Project project) {
        project.setOtherDocumentsApproved(ApprovalType.UNSET);
        project.setCollaborationAgreement(fileEntry);
    }

    private void removeCollaborationAgreementFileFromProject(Project project) {
        validateProjectIsInSetup(project).
        andOnSuccess(() -> project.setCollaborationAgreement(null));
    }

    private ServiceResult<FileEntry> getExploitationPlan(Project project) {
        if (project.getExploitationPlan() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getExploitationPlan());
        }
    }

    private FileEntryResource linkExploitationPlanFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        linkExploitationPlanFileEntryToProject(fileEntry, project);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private void linkExploitationPlanFileEntryToProject(FileEntry fileEntry, Project project) {
        project.setOtherDocumentsApproved(ApprovalType.UNSET);
        project.setExploitationPlan(fileEntry);
    }

    private void removeExploitationPlanFileFromProject(Project project) {

        validateProjectIsInSetup(project).
                andOnSuccess(() -> project.setExploitationPlan(null));
    }

    private NotificationTarget createProjectManagerNotificationTarget(final User projectManager) {
        String fullName = getProjectManagerFullName(projectManager);

        return new ExternalUserNotificationTarget(fullName, projectManager.getEmail());
    }


    private NotificationTarget createMonitoringOfficerNotificationTarget(MonitoringOfficerResource monitoringOfficer) {

        String fullName = getMonitoringOfficerFullName(monitoringOfficer);

        return new ExternalUserNotificationTarget(fullName, monitoringOfficer.getEmail());

    }

    private String getMonitoringOfficerFullName(MonitoringOfficerResource monitoringOfficer) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return monitoringOfficer.getFirstName() + " " + monitoringOfficer.getLastName();
    }

    private String getProjectManagerFullName(User projectManager) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private Map<String, Object> createGlobalArgsForMonitoringOfficerAssignedEmail(MonitoringOfficerResource monitoringOfficer, Project project, User projectManager) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("dashboardUrl", webBaseUrl);
        globalArguments.put("projectName", project.getName());
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        globalArguments.put("leadOrganisation", leadOrganisation.getName());
        globalArguments.put("projectManagerName", getProjectManagerFullName(projectManager));
        globalArguments.put("projectManagerEmail", projectManager.getEmail());
        globalArguments.put("monitoringOfficerName", getMonitoringOfficerFullName(monitoringOfficer));
        globalArguments.put("monitoringOfficerTelephone", monitoringOfficer.getPhoneNumber());
        globalArguments.put("monitoringOfficerEmail", monitoringOfficer.getEmail());
        return globalArguments;

    }

    private ServiceResult<Void> validateMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        if (!projectId.equals(monitoringOfficerResource.getProject())) {
            return serviceFailure(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE);
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<Void> validateInMonitoringOfficerAssignableState(final Long projectId) {

        return getProject(projectId).andOnSuccess(project -> {
            if (!projectDetailsWorkflowHandler.isSubmitted(project)) {
                return serviceFailure(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED);
            } else {
                return serviceSuccess();
            }
        });
    }

    private ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(final MonitoringOfficerResource monitoringOfficerResource) {

        return getExistingMonitoringOfficerForProject(monitoringOfficerResource.getProject()).handleSuccessOrFailure(
                noMonitoringOfficer -> saveNewMonitoringOfficer(monitoringOfficerResource),
                existingMonitoringOfficer -> updateExistingMonitoringOfficer(existingMonitoringOfficer, monitoringOfficerResource)
        );
    }

    private boolean isMonitoringOfficerDetailsChanged(MonitoringOfficer existingMonitoringOfficer, MonitoringOfficerResource updateDetails) {
        return !existingMonitoringOfficer.getFirstName().equals(updateDetails.getFirstName()) ||
                !existingMonitoringOfficer.getLastName().equals(updateDetails.getLastName()) ||
                !existingMonitoringOfficer.getEmail().equals(updateDetails.getEmail()) ||
                !existingMonitoringOfficer.getPhoneNumber().equals(updateDetails.getPhoneNumber());
    }

    private ServiceResult<SaveMonitoringOfficerResult> updateExistingMonitoringOfficer(MonitoringOfficer existingMonitoringOfficer, MonitoringOfficerResource updateDetails) {
        SaveMonitoringOfficerResult result = new SaveMonitoringOfficerResult();

        if (isMonitoringOfficerDetailsChanged(existingMonitoringOfficer, updateDetails)) {
            existingMonitoringOfficer.setFirstName(updateDetails.getFirstName());
            existingMonitoringOfficer.setLastName(updateDetails.getLastName());
            existingMonitoringOfficer.setEmail(updateDetails.getEmail());
            existingMonitoringOfficer.setPhoneNumber(updateDetails.getPhoneNumber());
        } else {
            result.setMonitoringOfficerSaved(false);
        }

        return serviceSuccess(result);
    }

    private ServiceResult<SaveMonitoringOfficerResult> saveNewMonitoringOfficer(MonitoringOfficerResource monitoringOfficerResource) {
        SaveMonitoringOfficerResult result = new SaveMonitoringOfficerResult();
        MonitoringOfficer monitoringOfficer = monitoringOfficerMapper.mapToDomain(monitoringOfficerResource);
        monitoringOfficerRepository.save(monitoringOfficer);
        return serviceSuccess(result);
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

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    private ServiceResult<Void> addFinanceContactToProject(Project project, ProjectUser newFinanceContact) {

        List<ProjectUser> existingFinanceContactForOrganisation = project.getProjectUsers(pu -> pu.getOrganisation().equals(newFinanceContact.getOrganisation()) && ProjectParticipantRole.PROJECT_FINANCE_CONTACT.equals(pu.getRole()));
        existingFinanceContactForOrganisation.forEach(project::removeProjectUser);
        project.addProjectUser(newFinanceContact);
        return serviceSuccess();
    }

    private ServiceResult<ProjectUser> createFinanceContactProjectUser(User user, Project project, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PROJECT_FINANCE_CONTACT);
    }

    @Override
    public ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
        return inviteContact(projectId, inviteResource, ProjectServiceImpl.Notifications.INVITE_FINANCE_CONTACT);
    }

    @Override
    public ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource) {
        return inviteContact(projectId, inviteResource, ProjectServiceImpl.Notifications.INVITE_PROJECT_MANAGER);
    }

    @Override
    public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
        Project project = projectRepository.findOne(projectId);
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());

        Optional<ProjectUser> partnerUserForFilterUser = filterByUserId.flatMap(
                userId -> simpleFindFirst(project.getProjectUsers(),
                        pu -> pu.getUser().getId().equals(userId) && pu.getRole().isPartner()));

        List<Organisation> partnerOrganisationsToInclude =
                simpleFilter(project.getOrganisations(), partner ->
                        partner.getId().equals(leadOrganisation.getId()) ||
                                (partnerUserForFilterUser.map(pu -> partner.getId().equals(pu.getOrganisation().getId()))
                                        .orElse(true)));

        List<Organisation> sortedOrganisationsToInclude
                = new PrioritySorting<>(partnerOrganisationsToInclude, leadOrganisation, Organisation::getName).unwrap();

        List<ProjectPartnerStatusResource> projectPartnerStatusResources =
                simpleMap(sortedOrganisationsToInclude, partner -> getProjectPartnerStatus(project, partner));

        ProjectTeamStatusResource projectTeamStatusResource = new ProjectTeamStatusResource();
        projectTeamStatusResource.setPartnerStatuses(projectPartnerStatusResources);

        return serviceSuccess(projectTeamStatusResource);
    }

    private ProjectPartnerStatusResource getProjectPartnerStatus(Project project, Organisation partnerOrganisation) {
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        Optional<MonitoringOfficer> monitoringOfficer = getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
        Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId()));
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId());
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(partnerOrganisation.getOrganisationType().getId());

        boolean isQueryActionRequired = financeCheckService.isQueryActionRequired(project.getId(),partnerOrganisation.getId()).getSuccessObject();

        ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, partnerOrganisation);
        ProjectActivityStates bankDetailsStatus = createBankDetailStatus(project.getId(), project.getApplication().getId(), partnerOrganisation.getId(), bankDetails, financeContactStatus);
        ProjectActivityStates financeChecksStatus = createFinanceCheckStatus(project, partnerOrganisation, isQueryActionRequired);
        ProjectActivityStates leadProjectDetailsSubmitted = createProjectDetailsStatus(project);
        ProjectActivityStates monitoringOfficerStatus = createMonitoringOfficerStatus(monitoringOfficer, leadProjectDetailsSubmitted);
        ProjectActivityStates spendProfileStatus = createSpendProfileStatus(financeChecksStatus, spendProfile);
        ProjectActivityStates leadSpendProfileStatus = createLeadSpendProfileStatus(project, spendProfileStatus, spendProfile);
        ProjectActivityStates otherDocumentsStatus = createOtherDocumentStatus(project);
        ProjectActivityStates grantOfferLetterStatus = createGrantOfferLetterStatus(leadSpendProfileStatus, otherDocumentsStatus, project, partnerOrganisation.equals(leadOrganisation));

        ProjectActivityStates partnerProjectDetailsSubmittedStatus = financeContactStatus;

        ProjectPartnerStatusResource projectPartnerStatusResource;

        if (partnerOrganisation.equals(leadOrganisation)) {
            projectPartnerStatusResource = new ProjectLeadStatusResource(
                    partnerOrganisation.getId(),
                    partnerOrganisation.getName(),
                    organisationType,
                    leadProjectDetailsSubmitted,
                    monitoringOfficerStatus,
                    bankDetailsStatus,
                    financeChecksStatus,
                    leadSpendProfileStatus,
                    otherDocumentsStatus,
                    grantOfferLetterStatus,
                    financeContactStatus,
                    golWorkflowHandler.isAlreadySent(project));
        } else {
            projectPartnerStatusResource = new ProjectPartnerStatusResource(
                    partnerOrganisation.getId(),
                    partnerOrganisation.getName(),
                    organisationType,
                    partnerProjectDetailsSubmittedStatus,
                    NOT_REQUIRED,
                    bankDetailsStatus,
                    financeChecksStatus,
                    spendProfileStatus,
                    NOT_REQUIRED,
                    grantOfferLetterStatus,
                    financeContactStatus,
                    golWorkflowHandler.isAlreadySent(project));
        }

        return projectPartnerStatusResource;
    }

    private ServiceResult<Void> inviteContact(Long projectId, InviteProjectResource projectResource, Notifications kindOfNotification) {

        ProjectInvite projectInvite = inviteProjectMapper.mapToDomain(projectResource);
        ServiceResult<Void> inviteContactEmailSendResult = projectEmailService.sendEmail(singletonList(createInviteContactNotificationTarget(projectInvite)), createGlobalArgsForInviteContactEmail(projectId, projectResource), kindOfNotification);
        inviteContactEmailSendResult.handleSuccessOrFailure(
                failure -> handleInviteError(projectInvite, failure),
                success -> handleInviteSuccess(projectInvite)
        );
        return inviteContactEmailSendResult;
    }

    private boolean handleInviteSuccess(ProjectInvite projectInvite) {
        inviteProjectRepository.save(projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }

    private ServiceResult<Boolean> handleInviteError(ProjectInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private NotificationTarget createInviteContactNotificationTarget(ProjectInvite projectInvite) {
        return new ExternalUserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInviteContactEmail(Long projectId, InviteProjectResource inviteResource) {
        Project project = projectRepository.findOne(projectId);
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        String leadOrganisationName = leadOrganisation.getName();
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", project.getName());
        globalArguments.put("leadOrganisation", leadOrganisationName);
        globalArguments.put("inviteOrganisationName", inviteResource.getOrganisationName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, inviteResource));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, InviteProjectResource inviteResource) {
        return String.format("%s/accept-invite/%s", baseUrl, inviteResource.getHash());
    }

    private ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH);
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE);
        }

        return serviceSuccess();
    }

    private ServiceResult<Project> validateProjectIsInSetup(final Project project) {
        if(!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }

    private ServiceResult<Project> validateIfProjectAlreadySubmitted(final Project project) {

        if (projectDetailsWorkflowHandler.isSubmitted(project)) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED);
        }

        return serviceSuccess(project);
    }

    private ServiceResult<ProjectUser> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

        ServiceResult<ProjectUser> result = find(organisation(organisationId))
                .andOnSuccessReturn(organisation -> project.getExistingProjectUserWithRoleForOrganisation(PROJECT_FINANCE_CONTACT, organisation));

        if (result.isFailure()) {
            return result;
        }

        List<ProjectUser> projectUsers = project.getProjectUsers();

        List<ProjectUser> matchingUserOrganisationProcessRoles = simpleFilter(projectUsers,
                pr -> organisationId.equals(pr.getOrganisation().getId()) && financeContactUserId.equals(pr.getUser().getId()));

        if (matchingUserOrganisationProcessRoles.isEmpty()) {
            return serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION);
        }

        List<ProjectUser> partnerUsers = simpleFilter(matchingUserOrganisationProcessRoles, ProjectUser::isPartner);

        if (partnerUsers.isEmpty()) {
            return serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION);
        }

        return getOnlyElementOrFail(partnerUsers);
    }

    private ServiceResult<ProjectUser> validateProjectManager(Project project, Long projectManagerUserId) {

        List<ProjectUser> leadPartners = getLeadPartners(project);
        List<ProjectUser> matchingProjectUsers = simpleFilter(leadPartners, pu -> pu.getUser().getId().equals(projectManagerUserId));

        if (!matchingProjectUsers.isEmpty()) {
            return getOnlyElementOrFail(matchingProjectUsers);
        } else {
            return serviceFailure(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER);
        }
    }

    private List<ProjectUser> getLeadPartners(Project project) {
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadPartnerOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        return simpleFilter(project.getProjectUsers(), pu -> organisationsEqual(leadPartnerOrganisation, pu)
                && pu.getRole().isPartner());
    }

    private boolean organisationsEqual(Organisation leadPartnerOrganisation, ProjectUser pu) {
        return pu.getOrganisation().getId().equals(leadPartnerOrganisation.getId());
    }

    private ServiceResult<ProjectResource> createSingletonProjectFromApplicationId(final Long applicationId) {

        return checkForExistingProjectWithApplicationId(applicationId).handleSuccessOrFailure(
                failure -> createProjectFromApplicationId(applicationId),
                success -> serviceSuccess(success)
        );
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

    private ServiceResult<ProjectResource> checkForExistingProjectWithApplicationId(Long applicationId) {
        return getByApplicationId(applicationId);
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

    private ServiceResult<Void> createProjectDetailsProcess(Project newProject, ProjectUser originalLeadApplicantProjectUser) {
        if (projectDetailsWorkflowHandler.projectCreated(newProject, originalLeadApplicantProjectUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_UNABLE_TO_CREATE_PROJECT_PROCESSES);
        }
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

    private ServiceResult<ProjectUser> createPartnerProjectUser(Project project, User user, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PROJECT_PARTNER);
    }

    private ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, ProjectParticipantRole role) {
        return serviceSuccess(new ProjectUser(user, project, role, organisation));
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }

    private ServiceResult<Project> getProjectByApplication(long applicationId) {
        return find(projectRepository.findOneByApplicationId(applicationId), notFoundError(Project.class, applicationId));
    }

    private ServiceResult<Void> createOrUpdateProjectManagerForProject(Project project, ProjectUser leadPartnerUser) {

        Optional<ProjectUser> existingProjectManager = getExistingProjectManager(project);

        ServiceResult<Void> setProjectManagerResult = existingProjectManager.map(pm -> {
            pm.setUser(leadPartnerUser.getUser());
            pm.setOrganisation(leadPartnerUser.getOrganisation());
            return serviceSuccess();

        }).orElseGet(() -> {
            ProjectUser projectUser = new ProjectUser(leadPartnerUser.getUser(), leadPartnerUser.getProcess(),
                    PROJECT_MANAGER, leadPartnerUser.getOrganisation());
            project.addProjectUser(projectUser);
            return serviceSuccess();
        });

        return setProjectManagerResult.andOnSuccessReturn(result ->
                projectDetailsWorkflowHandler.projectManagerAdded(project, leadPartnerUser)).andOnSuccess(workflowResult ->
                workflowResult ? serviceSuccess() : serviceFailure(PROJECT_SETUP_CANNOT_PROGRESS_WORKFLOW));
    }

    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }


    private List<NotificationTarget> getLiveProjectNotificationTarget(Project project) {
        List<NotificationTarget> notificationTargets = new ArrayList<>();
        User projectManager = getExistingProjectManager(project).get().getUser();
        NotificationTarget projectManagerTarget = createProjectManagerNotificationTarget(projectManager);
        List<NotificationTarget> financeTargets = simpleMap(simpleFilter(project.getProjectUsers(), pu -> pu.getRole().isFinanceContact()), pu -> new UserNotificationTarget(pu.getUser()));
        List<NotificationTarget> uniqueFinanceTargets = simpleFilterNot(financeTargets, target -> target.getEmailAddress().equals(projectManager.getEmail()));
        notificationTargets.add(projectManagerTarget);
        notificationTargets.addAll(uniqueFinanceTargets);

        return notificationTargets;
    }

    @Override
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {

        return getProject(projectId).andOnSuccess( project -> {
            if (project.getGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_AVAILABLE_BEFORE_SEND);
            }

            User projectManager = getExistingProjectManager(project).get().getUser();
            NotificationTarget pmTarget = createProjectManagerNotificationTarget(projectManager);

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("dashboardUrl", webBaseUrl);

            ServiceResult<Void> notificationResult = projectEmailService.sendEmail(singletonList(pmTarget), notificationArguments, ProjectServiceImpl.Notifications.GRANT_OFFER_LETTER_PROJECT_MANAGER);

            if (!notificationResult.isSuccess()) {
                return serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE);
            }
            return sendGrantOfferLetterSuccess(project);
        });
    }

    private ServiceResult<Void> sendGrantOfferLetterSuccess(Project project) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterSent(project, user)) {
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    private ServiceResult<Void> notifyProjectIsLive(Long projectId) {

        Project project = projectRepository.findOne(projectId);
        List<NotificationTarget> notificationTargets = getLiveProjectNotificationTarget(project);

        ServiceResult<Void> sendEmailResult = projectEmailService.sendEmail(notificationTargets, emptyMap(), Notifications.PROJECT_LIVE);

        return processAnyFailuresOrSucceed(sendEmailResult);
    }

    @Override
    public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {

        return getProject(projectId)
                .andOnSuccess(project -> {
                    if(!golWorkflowHandler.isSendAllowed(project)) {
                        return serviceSuccess(Boolean.FALSE);
                    }
                    return serviceSuccess(Boolean.TRUE);
                });
    }

    @Override
    public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return getProject(projectId)
                .andOnSuccess(project -> {
                    if(!golWorkflowHandler.isAlreadySent(project)) {
                        return serviceSuccess(Boolean.FALSE);
                    }
                    return serviceSuccess(Boolean.TRUE);
                });
    }

    @Override
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {

        return getProject(projectId).andOnSuccess( project -> {
            if(golWorkflowHandler.isReadyToApprove(project)) {
                if(ApprovalType.APPROVED == approvalType) {
                    return approveGOL(project)
                        .andOnSuccess(() -> {

                            if (!projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))) {
                                LOG.error(String.format(PROJECT_STATE_ERROR, project.getId()));
                                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
                            }
                            notifyProjectIsLive(projectId);
                            return serviceSuccess();
                        }
                    );
                }
            }
            return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_NOT_READY_TO_APPROVE);
        });
    }

    private ServiceResult<Void> approveGOL(Project project) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterApproved(project, user)) {
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    @Override
    public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(golWorkflowHandler::isApproved);
    }

    @Override
    public ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(project -> golWorkflowHandler.getState(project));
    }

    @Override
    public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
        return find(projectUserRepository.findByProjectIdAndRole(projectId, ProjectParticipantRole.PROJECT_MANAGER),
            notFoundError(ProjectUserResource.class, projectId)).andOnSuccessReturn(projectUserMapper::mapToResource);
    }
}
