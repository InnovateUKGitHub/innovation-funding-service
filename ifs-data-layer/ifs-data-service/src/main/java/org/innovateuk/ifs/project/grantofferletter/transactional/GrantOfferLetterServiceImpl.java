package org.innovateuk.ifs.project.grantofferletter.transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FileTemplateRenderer;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.project.financechecks.repository.CostRepository;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.model.*;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static java.io.File.separator;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Service
public class GrantOfferLetterServiceImpl extends BaseTransactionalService implements GrantOfferLetterService {

    private static final String GOL_CONTENT_TYPE = "application/pdf";

    private static final String DEFAULT_GOL_NAME = "grant_offer_letter.pdf";

    private static final Long DEFAULT_GOL_SIZE = 1L;

    private static final String GRANT_OFFER_LETTER_DATE_FORMAT = "d MMMM yyyy";

    private static final Log LOG = LogFactory.getLog(GrantOfferLetterServiceImpl.class);

    private static final String GOL_STATE_ERROR = "Set Grant Offer Letter workflow status to sent failed for project %s";

    private static final String PROJECT_STATE_ERROR = "Set project status to live failed for project %s";

    private static final String GOL_TEMPLATES_PATH = "common" + separator + "grantoffer" + separator + "grant_offer_letter.html";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FileTemplateRenderer fileTemplateRenderer;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private GrantOfferLetterIndustrialFinanceTablePopulator grantOfferLetterIndustrialFinanceTablePopulator;

    @Autowired
    private GrantOfferLetterAcademicFinanceTablePopulator grantOfferLetterAcademicFinanceTablePopulator;

    @Autowired
    private GrantOfferLetterFinanceTotalsTablePopulator grantOfferLetterFinanceTotalsTablePopulator;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public enum NotificationsGol {
        GRANT_OFFER_LETTER_PROJECT_MANAGER,
        PROJECT_LIVE
    }

    @Override
    public ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getSignedGrantOfferLetter();
            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getGrantOfferLetter();
            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getAdditionalContractFile();
            return getFileAndContentsResult(fileEntry);
        });
    }

    private FailingOrSucceedingResult<FileAndContents, ServiceFailure> getFileAndContentsResult(FileEntry fileEntry) {
        if (fileEntry == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        }

        ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
        return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
    }

    @Override
    public ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getSignedGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    public ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getAdditionalContractFile();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));

    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> generateGrantOfferLetter(Long projectId, FileEntryResource fileEntryResource) {

        return getProject(projectId).
                andOnSuccess(project -> fileTemplateRenderer.renderTemplate(getTemplatePath(), getTemplateData(project)).
                        andOnSuccess(htmlFile -> convertHtmlToPdf(() -> new ByteArrayInputStream(StringUtils.getBytesUtf8(htmlFile)),
                                fileEntryResource).
                                andOnSuccess(inputStreamSupplier -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)))));
    }

    private String getTemplatePath() {
        return GOL_TEMPLATES_PATH;
    }

    private Map<String, Object> getTemplateData(Project project) {
        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadProcessRole.getOrganisationId());

        Map<Organisation, List<Cost>> financesForOrgs = getFinances(project);
        GrantOfferLetterIndustrialFinanceTable industrialFinanceTable = grantOfferLetterIndustrialFinanceTablePopulator.createTable(financesForOrgs);
        GrantOfferLetterAcademicFinanceTable academicFinanceTable = grantOfferLetterAcademicFinanceTablePopulator.createTable(financesForOrgs);
        GrantOfferLetterFinanceTotalsTable totalsFinanceTable = grantOfferLetterFinanceTotalsTablePopulator.createTable(financesForOrgs, project.getId());

        final Map<String, Object> templateReplacements = new HashMap<>();
        final List<String> addresses = getAddresses(project);
        templateReplacements.put("LeadContact", project.getApplication().getLeadApplicant().getName());
        templateReplacements.put("LeadOrgName", leadOrganisation.getName());
        templateReplacements.put("Address1", addresses.isEmpty() ? "" : addresses.get(0));
        templateReplacements.put("Address2", addresses.size() < 2 ? "" : addresses.get(1));
        templateReplacements.put("Address3", addresses.size() < 3 ? "" : addresses.get(2));
        templateReplacements.put("TownCity", addresses.size() < 4 ? "" : addresses.get(3));
        templateReplacements.put("PostCode", addresses.size() < 5 ? "" : addresses.get(4));
        templateReplacements.put("Date", ZonedDateTime.now().toString());
        templateReplacements.put("CompetitionName", project.getApplication().getCompetition().getName());
        templateReplacements.put("ProjectTitle", project.getName());
        templateReplacements.put("ProjectStartDate", project.getTargetStartDate() != null ?
                project.getTargetStartDate().format(DateTimeFormatter.ofPattern(GRANT_OFFER_LETTER_DATE_FORMAT)) : "");
        templateReplacements.put("ProjectLength", project.getDurationInMonths());
        templateReplacements.put("ApplicationNumber", project.getApplication().getId());

        // add finances tables
        templateReplacements.put("industrialFinanceTable", industrialFinanceTable);
        templateReplacements.put("academicFinanceTable", academicFinanceTable);
        templateReplacements.put("financeTotalsTable", totalsFinanceTable);

        return templateReplacements;
    }

    private Map<Organisation, List<Cost>> getFinances(Project project) {
        Map<Organisation, List<Cost>> orgFinances = new HashMap<>();

        project.getOrganisations()
                .forEach(org -> {
                    SpendProfile orgSpendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), org.getId()).get();
                    CostGroup orgCostGroup = orgSpendProfile.getSpendProfileFigures();
                    List<Cost> costs = costRepository.findByCostGroupId(orgCostGroup.getId());

                    orgFinances.put(org, costs);
                });

        return orgFinances;
    }

    private ServiceResult<Supplier<InputStream>> convertHtmlToPdf(Supplier<InputStream> inputStreamSupplier, FileEntryResource fileEntryResource) {
        try {
            return createPDF("", inputStreamSupplier, fileEntryResource);
        } catch (IOException e) {
            LOG.error("An IO Exception occurred" + e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        } catch (DocumentException e) {
            LOG.error("A Document Exception occured" + e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        }
    }

    private static ServiceResult<Supplier<InputStream>> createPDF(String url, Supplier<InputStream> inputStreamSupplier, FileEntryResource fileEntryResource)
            throws IOException, DocumentException {

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();
            Document doc = XMLResource.load(new InputSource(inputStreamSupplier.get())).getDocument();

            PdfWriter writer = renderer.getWriter();
            if (writer != null) {
                writer.setPDFXConformance(PdfWriter.PDFA1A);
            }

            renderer.setDocument(doc, url);
            renderer.layout();
            renderer.createPDF(os);
            fileEntryResource.setFilesizeBytes(os.toByteArray().length);
            return ServiceResult.serviceSuccess(() -> new ByteArrayInputStream(os.toByteArray()));
        }
    }

    private List<String> getAddresses(Project project) {
        List<String> addressLines = new ArrayList<>();
        if (project.getAddress() != null) {
            Address address = project.getAddress();
            addressLines.add(address.getAddressLine1() != null ? address.getAddressLine1() : "");
            addressLines.add(address.getAddressLine2() != null ? address.getAddressLine2() : "");
            addressLines.add((address.getAddressLine3() != null ? address.getAddressLine3() : ""));
            addressLines.add(address.getTown() != null ? address.getTown() : "");
            addressLines.add(address.getPostcode() != null ? address.getPostcode() : "");
        }
        return addressLines;
    }

    @Override
    @Transactional
    public ServiceResult<Void> generateGrantOfferLetterIfReady(Long projectId) {
        if (isProjectReadyForGrantOffer(projectId)) {
            FileEntryResource generatedGrantOfferLetterFileEntry = new FileEntryResource(null, DEFAULT_GOL_NAME, GOL_CONTENT_TYPE, DEFAULT_GOL_SIZE);
            return generateGrantOfferLetter(projectId, generatedGrantOfferLetterFileEntry)
                    .andOnSuccess(() -> serviceSuccess()).
                            andOnFailure(() -> serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        } else {
            return serviceSuccess();
        }
    }

    private boolean isProjectReadyForGrantOffer(Long projectId) {
        Optional<Project> project = getProject(projectId).getOptionalSuccessObject();
        ApprovalType spendProfileApproval = spendProfileService.getSpendProfileStatusByProjectId(projectId).getSuccess();

        return project.map(project1 -> ApprovalType.APPROVED.equals(spendProfileApproval) && ApprovalType.APPROVED.equals(project1.getOtherDocumentsApproved()) && project1.getGrantOfferLetter() == null).orElse(false);
    }

    private FileEntryResource linkGrantOfferLetterFileToProject(Project project, Pair<File, FileEntry> fileDetails, boolean signed) {
        FileEntry fileEntry = fileDetails.getValue();

        if (signed) {
            project.setSignedGrantOfferLetter(fileEntry);
        } else {
            project.setGrantOfferLetter(fileEntry);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsInSetup)
                .andOnSuccess(project ->
                        validateRemoveGrantOfferLetter(project).andOnSuccess(() ->
                                getGrantOfferLetterFileEntry(project).andOnSuccess(fileEntry ->
                                        fileService.deleteFileIgnoreNotFound(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                                removeGrantOfferLetterFileFromProject(project)))));
    }

    private ServiceResult<Void> validateRemoveGrantOfferLetter(Project project) {
        return getCurrentlyLoggedInUser().andOnSuccess(user ->
                golWorkflowHandler.removeGrantOfferLetter(project, user) ?
                        serviceSuccess() : serviceFailure(GRANT_OFFER_LETTER_CANNOT_BE_REMOVED));
    }

    private ServiceResult<FileEntry> getGrantOfferLetterFileEntry(Project project) {
        if (project.getGrantOfferLetter() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getGrantOfferLetter());
        }
    }

    private void removeGrantOfferLetterFileFromProject(Project project) {
        validateProjectIsInSetup(project).andOnSuccess(() ->
                project.setGrantOfferLetter(null));
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeSignedGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsInSetup)
                .andOnSuccess(project -> getCurrentlyLoggedInUser().andOnSuccess(user -> {

                        if (!golWorkflowHandler.removeSignedGrantOfferLetter(project, user)) {
                            return serviceFailure(GRANT_OFFER_LETTER_CANNOT_BE_REMOVED);
                        }

                        return getSignedGrantOfferLetterFileEntry(project).andOnSuccess(fileEntry ->
                                fileService.deleteFileIgnoreNotFound(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                        removeSignedGrantOfferLetterFileFromProject(project)));
                }));
    }

    private ServiceResult<FileEntry> getSignedGrantOfferLetterFileEntry(Project project) {
        if (project.getSignedGrantOfferLetter() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getSignedGrantOfferLetter());
        }
    }

    private void removeSignedGrantOfferLetterFileFromProject(Project project) {
        validateProjectIsInSetup(project).andOnSuccess(() ->
                project.setSignedGrantOfferLetter(null));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkAdditionalContractFileToProject(project, fileDetails)));

    }

    private FileEntryResource linkAdditionalContractFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        project.setAdditionalContractFile(fileEntry);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> {
                    if (golWorkflowHandler.isSent(project)) {
                        return fileService.updateFile(fileEntryResource, inputStreamSupplier).
                                andOnSuccessReturnVoid(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true));
                    } else {
                        return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY);
                    }
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSignedGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.SIGNED_GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT);
            }
            if (!golWorkflowHandler.sign(project)) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_SET_SIGNED_STATE);
            }
            project.setOfferSubmittedDate(ZonedDateTime.now());
            return serviceSuccess();
        });
    }

    private ServiceResult<Project> validateProjectIsInSetup(final Project project) {
        if (!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {

        return getProject(projectId).andOnSuccess(project -> {
            if (project.getGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_AVAILABLE_BEFORE_SEND);
            }

            User projectManager = getExistingProjectManager(project).get().getUser();
            NotificationTarget pmTarget = createProjectManagerNotificationTarget(projectManager);

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("dashboardUrl", webBaseUrl);
            notificationArguments.put("applicationId", project.getApplication().getId());
            notificationArguments.put("competitionName", project.getApplication().getCompetition().getName());

            return sendGrantOfferLetterSuccess(project).andOnSuccess(() -> {
                Notification notification = new Notification(systemNotificationSource,
                                                             singletonList(pmTarget),
                                                             NotificationsGol.GRANT_OFFER_LETTER_PROJECT_MANAGER,
                                                             notificationArguments);

                return notificationService.sendNotificationWithFlush(notification, EMAIL);
            });
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

    @Override
    @Transactional
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {

        return validateApprovalOrRejection(grantOfferLetterApprovalResource).andOnSuccess(() ->
            getProject(projectId).andOnSuccess(project -> {
                if (golWorkflowHandler.isReadyToApprove(project)) {
                    if (ApprovalType.APPROVED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
                        return approveGOL(project)
                                .andOnSuccess(() -> moveProjectToLiveState(project));
                    } else if (ApprovalType.REJECTED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
                        return rejectGOL(project, grantOfferLetterApprovalResource.getRejectionReason());
                    }
                }
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_NOT_READY_TO_APPROVE);
        }));
    }

    private ServiceResult<Void> validateApprovalOrRejection(GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
        if (ApprovalType.REJECTED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(grantOfferLetterApprovalResource.getRejectionReason())) {
                return serviceSuccess();
            }
        } else if (ApprovalType.APPROVED.equals(grantOfferLetterApprovalResource.getApprovalType())) {
            return serviceSuccess();
        }

        return serviceFailure(GENERAL_INVALID_ARGUMENT);
    }

    private ServiceResult<Void> moveProjectToLiveState(Project project) {

        if (!projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))) {
            LOG.error(String.format(PROJECT_STATE_ERROR, project.getId()));
            return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
        }

        return notifyProjectIsLive(project.getId());
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

    private ServiceResult<Void> rejectGOL(Project project, String golRejectionReason) {

        return getCurrentlyLoggedInUser().andOnSuccess(user -> {

            if (golWorkflowHandler.grantOfferLetterRejected(project, user)) {
                project.setOfferSubmittedDate(null);
                project.setGrantOfferLetterRejectionReason(golRejectionReason);
                return serviceSuccess();
            } else {
                LOG.error(String.format(GOL_STATE_ERROR, project.getId()));
                return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
            }
        });
    }

    @Override
    public ServiceResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId) {
        return getProject(projectId).andOnSuccess(
                golWorkflowHandler::getExtendedState);
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
        List<NotificationTarget> financeTargets = simpleMap(simpleFilter(project.getProjectUsers(), pu -> pu.getRole().isFinanceContact()), pu -> new UserNotificationTarget(pu.getUser().getName(), pu.getUser().getEmail()));
        List<NotificationTarget> uniqueFinanceTargets = simpleFilterNot(financeTargets, target -> target.getEmailAddress().equals(projectManager.getEmail()));
        notificationTargets.add(projectManagerTarget);
        notificationTargets.addAll(uniqueFinanceTargets);

        return notificationTargets;
    }

    private NotificationTarget createProjectManagerNotificationTarget(final User projectManager) {
        String fullName = getProjectManagerFullName(projectManager);

        return new UserNotificationTarget(fullName, projectManager.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private ServiceResult<Void> notifyProjectIsLive(Long projectId) {

        Project project = projectRepository.findOne(projectId);
        List<NotificationTarget> notificationTargets = getLiveProjectNotificationTarget(project);

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", project.getApplication().getId());
        notificationArguments.put("competitionName", project.getApplication().getCompetition().getName());

        Notification notification = new Notification(systemNotificationSource, notificationTargets, NotificationsGol.PROJECT_LIVE, notificationArguments);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }
}
