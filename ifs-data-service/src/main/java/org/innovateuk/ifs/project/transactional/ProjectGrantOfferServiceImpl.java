package org.innovateuk.ifs.project.transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
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
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileService;
import org.innovateuk.ifs.project.gol.YearlyGOLProfileTable;
import org.innovateuk.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static java.io.File.separator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapValue;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectGrantOfferServiceImpl extends BaseTransactionalService implements ProjectGrantOfferService {

    static final String GOL_TEMPLATES_PATH = "common" + separator + "grantoffer" + separator + "grant_offer_letter.html";

    public static final String GOL_CONTENT_TYPE = "application/pdf";

    public static final String DEFAULT_GOL_NAME = "grant_offer_letter.pdf";

    public static final Long DEFAULT_GOL_SIZE = 1L;

    private static final Log LOG = LogFactory.getLog(ProjectGrantOfferServiceImpl.class);

    public static final String GRANT_OFFER_LETTER_DATE_FORMAT = "d MMMM yyyy";

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
    private ProjectMapper projectMapper;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;


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

    private FailingOrSucceedingResult<FileAndContents, ServiceFailure> getFileAndContentsResult(FileEntry fileEntry) {
        if (fileEntry == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        }

        ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
        return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
    }

    @Override
    public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            FileEntry fileEntry = project.getAdditionalContractFile();
            return getFileAndContentsResult(fileEntry);
        });
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
    public ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));

    }

    @Override
    public ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)));
    }

    @Override
    public ServiceResult<FileEntryResource> generateGrantOfferLetter(Long projectId, FileEntryResource fileEntryResource) {

        return getProject(projectId).
                andOnSuccess(project -> fileTemplateRenderer.renderTemplate(getTemplatePath(), getTemplateData(project)).
                        andOnSuccess(htmlFile -> convertHtmlToPdf(() -> new ByteArrayInputStream(StringUtils.getBytesUtf8(htmlFile)),
                                fileEntryResource).
                                andOnSuccess(inputStreamSupplier -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)))));
    }

    private boolean isProjectReadyForGrantOffer(Long projectId) {
        Optional<Project> project = getProject(projectId).getOptionalSuccessObject();
        ApprovalType spendProfileApproval = spendProfileService.getSpendProfileStatusByProjectId(projectId).getSuccessObject();

        return project.map(project1 -> ApprovalType.APPROVED.equals(spendProfileApproval) && ApprovalType.APPROVED.equals(project1.getOtherDocumentsApproved()) && project1.getGrantOfferLetter() == null).orElse(false);
    }

    @Override
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

    private Map<String, Object> getTemplateData(Project project) {
        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadProcessRole.getOrganisationId());
        final Map<String, Object> templateReplacements = new HashMap<>();
        final List<String> addresses = getAddresses(project);
        List<String> organisationNames = new LinkedList<>();
        YearlyGOLProfileTable yearlyGolProfileTable = getYearlyGOLProfileTableExcludingNonAcademicUnfundedNonLeadPartners(project, leadOrganisation, organisationNames);
        templateReplacements.put("SortedOrganisations", sortedOrganisations(organisationNames, leadOrganisation.getName()));
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
        templateReplacements.put("TableData", yearlyGolProfileTable);
        return templateReplacements;
    }

    private List<String> sortedOrganisations(List<String> orgs, String lead) {
        return new PrioritySorting<>(orgs, lead, identity()).unwrap().stream().map(StringEscapeUtils::escapeXml10).collect(toList());
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


    private ServiceResult<Supplier<InputStream>> convertHtmlToPdf(Supplier<InputStream> inputStreamSupplier, FileEntryResource fileEntryResource) {
        ServiceResult<Supplier<InputStream>> pdfSupplier = null;
        try {
            pdfSupplier = createPDF("", inputStreamSupplier, fileEntryResource);
        } catch (IOException e) {
            LOG.error("An IO Exception occured" + e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        } catch (DocumentException e) {
            LOG.error("A Document Exception occured" + e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        }
        return pdfSupplier;
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
    public ServiceResult<Void> removeGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsInSetup)
                .andOnSuccess(project ->
                        validateRemoveGrantOfferLetter(project).andOnSuccess(() ->
                                getGrantOfferLetterFileEntry(project).andOnSuccess(fileEntry ->
                                        fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
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
    public ServiceResult<Void> removeSignedGrantOfferLetterFileEntry(Long projectId) {
        return getProject(projectId).andOnSuccess(this::validateProjectIsInSetup)
                .andOnSuccess(project ->
                        getSignedGrantOfferLetterFileEntry(project).andOnSuccess(fileEntry ->
                                fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                        removeSignedGrantOfferLetterFileFromProject(project))));
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
    public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkAdditionalContractFileToProject(project, fileDetails)));

    }

    @Override
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

    private FileEntryResource linkAdditionalContractFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        project.setAdditionalContractFile(fileEntry);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

    private String getTemplatePath() {
        return GOL_TEMPLATES_PATH;
    }

    private YearlyGOLProfileTable getYearlyGOLProfileTableExcludingNonAcademicUnfundedNonLeadPartners(Project project, Organisation leadOrganisation, List<String> includedOrganisationNames) {
        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap<>();
        Map<String, List<String>> organisationYearsMap = new LinkedHashMap<>();
        Map<String, List<BigDecimal>> organisationEligibleCostTotal = new HashMap<>();
        Map<String, List<BigDecimal>> organisationGrantAllocationTotal = new HashMap<>();

        List<String> projectYears = spendProfileTableCalculator.generateSpendProfileYears(projectMapper.mapToResource(project));

        List<Organisation> organisations = project.getOrganisations();
        Map<String, SpendProfileTableResource> organisationSpendProfiles = getSpendProfileTableResourcePerOrganisation(project, organisations);

        Map<String, List<BigDecimal>> monthlyCostsPerOrganisationMap = getMonthlyEligibleCostPerOrganisation(organisationSpendProfiles);
        List<LocalDateResource> months = organisationSpendProfiles.values().iterator().next().getMonths();

        populateOrganisationPerValuesMaps(project, organisationAndGrantPercentageMap,
                organisationYearsMap, organisationEligibleCostTotal, organisationGrantAllocationTotal,
                projectYears, monthlyCostsPerOrganisationMap,
                months);

        Map<String, BigDecimal> yearEligibleCostTotal = spendProfileTableCalculator.createYearlyEligibleCostTotal(projectMapper.mapToResource(project), monthlyCostsPerOrganisationMap, months);
        FinanceCheckSummaryResource financeCheckSummary = financeCheckService.getFinanceCheckSummary(project.getId()).getSuccessObject();
        Map<String, BigDecimal> yearlyGrantAllocationTotal = spendProfileTableCalculator.createYearlyGrantAllocationTotal(projectMapper.mapToResource(project),
                monthlyCostsPerOrganisationMap, months, financeCheckSummary.getTotalPercentageGrant());

        Set<Organisation> organisationsExcludedFromGrantOfferLetter = project.getOrganisations().stream().filter(organisation ->
                !financeUtil.isUsingJesFinances(organisation.getOrganisationType().getId())
                        && !organisation.getName().equals(leadOrganisation.getName())
                        && organisationAndGrantPercentageMap.get(organisation.getName()).equals(0)
                        && organisationGrantAllocationTotal.get(organisation.getName()).stream().reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(BigDecimal.ZERO) == 0)
                .collect(toCollection(HashSet::new));
        organisationsExcludedFromGrantOfferLetter.forEach(excludedOrganisation -> {
            organisationAndGrantPercentageMap.remove(excludedOrganisation.getName());
            organisationYearsMap.remove(excludedOrganisation.getName());
            organisationEligibleCostTotal.remove(excludedOrganisation.getName());
            organisationGrantAllocationTotal.remove(excludedOrganisation.getName());
            yearEligibleCostTotal.remove(excludedOrganisation.getName());
            yearlyGrantAllocationTotal.remove(excludedOrganisation.getName());
        });

        includedOrganisationNames.clear();
        List<Organisation> includedOrganisations = project.getOrganisations().stream().filter(organisation ->
                !organisationsExcludedFromGrantOfferLetter.contains(organisation))
                .collect(toList());

        includedOrganisations.forEach(includedOrg -> includedOrganisationNames.add(includedOrg.getName()));

        return new YearlyGOLProfileTable(escapeXml10(organisationAndGrantPercentageMap),
                                         escapeXml10(organisationYearsMap),
                                         escapeXml10(organisationEligibleCostTotal),
                                         escapeXml10(organisationGrantAllocationTotal),
                                         escapeXml10(yearEligibleCostTotal),
                                         escapeXml10(yearlyGrantAllocationTotal));
    }

    private <T> Map<String, T>escapeXml10(Map<String, T> unescapedMap) {
        Map<String, T> escapedMap = new HashMap<>();
        unescapedMap.forEach((organisation, value) -> escapedMap.put(StringEscapeUtils.escapeXml10(organisation), value));
        return escapedMap;
    }

    private Map<String, List<BigDecimal>> getMonthlyEligibleCostPerOrganisation(Map<String,
            SpendProfileTableResource> organisationSpendProfiles) {
        return simpleMapValue(organisationSpendProfiles, tableResource ->
                spendProfileTableCalculator.calculateMonthlyTotals(tableResource.getMonthlyCostsPerCategoryMap(),
                        tableResource.getMonths().size()));
    }

    private Map<String, SpendProfileTableResource> getSpendProfileTableResourcePerOrganisation(Project project,
                                                                                               List<Organisation> organisations) {
        return organisations.stream()
                .collect(toMap(Organisation::getName, organisation -> {
                    ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
                    if (spendProfileService.getSpendProfileTable(projectOrganisationCompositeId).isSuccess()) {
                        return spendProfileService.getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
                    }
                    return new SpendProfileTableResource();
                }));
    }

    private void populateOrganisationPerValuesMaps(Project project,
                                                   Map<String, Integer> organisationAndGrantPercentageMap,
                                                   Map<String, List<String>> organisationYearsMap,
                                                   Map<String, List<BigDecimal>> organisationEligibleCostTotal,
                                                   Map<String, List<BigDecimal>> organisationGrantAllocationTotal,
                                                   List<String> projectYears,
                                                   Map<String, List<BigDecimal>> monthlyCostsPerOrganisationMap,
                                                   List<LocalDateResource> months) {


        monthlyCostsPerOrganisationMap.entrySet().stream().forEach(entry -> {
            Organisation organisation = getOrganisationFrom(entry.getKey(), project);
            financeRowService.financeDetails(project.getApplication().getId(), organisation.getId())
                    .andOnSuccessReturn(applicationFinanceResource -> grantPercentagePerOrganisation(applicationFinanceResource,
                            organisation,
                            organisationAndGrantPercentageMap));

            List<BigDecimal> eligibleCostPerYear = spendProfileTableCalculator.calculateEligibleCostPerYear(projectMapper.mapToResource(project), entry.getValue(), months);
            List<BigDecimal> grantAllocationPerYear = spendProfileTableCalculator.calculateGrantAllocationPerYear(projectMapper.mapToResource(project), entry.getValue(), months,
                    organisationAndGrantPercentageMap.get(organisation.getName()));

            organisationEligibleCostTotal.put(organisation.getName(), eligibleCostPerYear);
            organisationGrantAllocationTotal.put(organisation.getName(), grantAllocationPerYear);
            organisationYearsMap.put(organisation.getName(), projectYears);
        });
    }

    private Organisation getOrganisationFrom(String name, Project project) {
        return project.getOrganisations().stream().filter(organisation ->
                organisation.getName().equals(name)).findFirst().get();
    }

    private ServiceResult<Void> grantPercentagePerOrganisation(ApplicationFinanceResource applicationFinanceResource, Organisation organisation, Map<String, Integer> organisationAndGrantPercentageMap) {
        organisationAndGrantPercentageMap.put(organisation.getName(),
                financeUtil.isUsingJesFinances(organisation.getOrganisationType().getId())
                        ? 100 : applicationFinanceResource.getGrantClaimPercentage());
        return serviceSuccess();
    }

    private ServiceResult<Project> validateProjectIsInSetup(final Project project) {
        if (!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }
}
