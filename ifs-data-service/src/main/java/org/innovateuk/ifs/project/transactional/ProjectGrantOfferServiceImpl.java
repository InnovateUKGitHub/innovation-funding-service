package org.innovateuk.ifs.project.transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
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
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.gol.YearlyGOLProfileTable;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static java.io.File.separator;

@Service
public class ProjectGrantOfferServiceImpl extends BaseTransactionalService implements ProjectGrantOfferService{

    static final String GOL_TEMPLATES_PATH = "common" + separator + "grantoffer" + separator + "grant_offer_letter.html";

    public static final String GOL_CONTENT_TYPE = "application/pdf";

    public static final String DEFAULT_GOL_NAME = "grant_offer_letter.pdf";

    public static final Long DEFAULT_GOL_SIZE = 1L;

    private static final Log LOG = LogFactory.getLog(ProjectGrantOfferServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FileTemplateRenderer fileTemplateRenderer;


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

        //TODO Implement adding Finance data if approved otherwise skip generation of GOL.
        return getProject(projectId).
                andOnSuccess(project -> fileTemplateRenderer.renderTemplate(getTemplatePath(), getTemplateData(project)).
                            andOnSuccess(htmlFile -> convertHtmlToPdf(() -> new ByteArrayInputStream(StringUtils.getBytesUtf8(htmlFile)),
                                    fileEntryResource).
                                    andOnSuccess(inputStreamSupplier -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                                            andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)))));
    }

    @Override
    public ServiceResult<Void> generateGrantOfferLetterIfReady(Long projectId) {
        return projectFinanceService.getSpendProfileStatusByProjectId(projectId).andOnSuccess(approval -> {
            if(approval == ApprovalType.APPROVED) {
                return getProject(projectId).andOnSuccess(project -> {
                    if (project.getOtherDocumentsApproved() != null && project.getOtherDocumentsApproved()) {

                        FileEntryResource generatedGrantOfferLetterFileEntry = new FileEntryResource(null, DEFAULT_GOL_NAME, GOL_CONTENT_TYPE, DEFAULT_GOL_SIZE);
                        return generateGrantOfferLetter(projectId, generatedGrantOfferLetterFileEntry)
                                .andOnSuccess(() -> serviceSuccess()).
                                        andOnFailure(() -> serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
                    } else {
                        return serviceSuccess();
                    }
                });
            } else {
                return serviceSuccess();
            }
        });
    }

    private Map<String, Object> getTemplateData(Project project) {
        Map<String, Object> templateReplacements = new HashMap<>();
        Address address = project.getAddress();
        templateReplacements.put("LeadContact", project.getApplication().getLeadApplicant().getName());
        templateReplacements.put("LeadOrgName", project.getApplication().getLeadOrganisation().getName());
        templateReplacements.put("Address1", address != null && address.getAddressLine1() != null ? address.getAddressLine1() : "");
        templateReplacements.put("Address2", address != null && address.getAddressLine2() != null ? address.getAddressLine2() : "");
        templateReplacements.put("Address3", address != null && address.getAddressLine3() != null ? address.getAddressLine3() : "");
        templateReplacements.put("TownCity", address != null && address.getTown() != null ? address.getTown() : "");
        templateReplacements.put("PostCode", address != null && address.getPostcode() != null ? address.getPostcode() : "");
        templateReplacements.put("Date", LocalDateTime.now().toString());
        templateReplacements.put("CompetitionName", project.getApplication().getCompetition().getName());
        templateReplacements.put("ProjectTitle", project.getName());
        templateReplacements.put("ProjectStartDate", project.getTargetStartDate() != null ?
                project.getTargetStartDate().toString() : "");
        templateReplacements.put("ProjectLength", project.getDurationInMonths());
        templateReplacements.put("ApplicationNumber", project.getApplication().getId());
        templateReplacements.put("TableData", getYearlyGOLProfileTable());
        return templateReplacements;
    }

    private ServiceResult<Supplier<InputStream>> convertHtmlToPdf(Supplier<InputStream> inputStreamSupplier, FileEntryResource fileEntryResource) {
        ServiceResult<Supplier<InputStream>> pdfSupplier = null;
        try {
            pdfSupplier = createPDF("", inputStreamSupplier, fileEntryResource);
        } catch (IOException e) {
            LOG.error("An IO Exception occured" +e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        } catch (DocumentException e) {
            LOG.error("A Document Exception occured" +e);
            return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF));
        }
        return pdfSupplier;
    }

    private static ServiceResult<Supplier<InputStream>> createPDF(String url, Supplier<InputStream> inputStreamSupplier, FileEntryResource fileEntryResource)
            throws IOException, DocumentException {

        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {

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
        return getProject(projectId).andOnSuccess(project ->
                getGrantOfferLetterFileEntry(project).andOnSuccess(fileEntry ->
                        fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                                removeGrantOfferLetterFileFromProject(project))));
    }

    private ServiceResult<FileEntry> getGrantOfferLetterFileEntry(Project project) {
        if (project.getGrantOfferLetter() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(project.getGrantOfferLetter());
        }
    }

    private void removeGrantOfferLetterFileFromProject(Project project) {
        project.setGrantOfferLetter(null);
    }

    @Override
    public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkAdditionalContractFileToProject(project, fileDetails)));

    }

    @Override
    public ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));
    }

    @Override
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSignedGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.SIGNED_GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT);
            }
            project.setOfferSubmittedDate(LocalDateTime.now());
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

    private YearlyGOLProfileTable getYearlyGOLProfileTable() {

        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap();
        Map<String, List<String>> organisationYearsMap = new LinkedHashMap();
        List<String> years = new LinkedList<>();
        Map<String, List<BigDecimal>> organisationEligibleCostTotal = new HashMap();
        List<BigDecimal> eligibleCostPerYear = new LinkedList<>();
        Map<String, List<BigDecimal>> organisationGrantAllocationTotal = new HashMap();
        List<BigDecimal> grantPerYear = new LinkedList<>();
        Map<String, BigDecimal> yearEligibleCostTotal = new HashMap();
        Map<String, BigDecimal> yearGrantAllocationTotal = new HashMap();
        BigDecimal eligibleCostGrandTotal = BigDecimal.valueOf(10000);
        BigDecimal grantAllocationGrandTotal = BigDecimal.valueOf(10000);

        organisationAndGrantPercentageMap.put("Empire Ltd", 20);
        organisationAndGrantPercentageMap.put("Ludlow", 30);
        organisationAndGrantPercentageMap.put("EGGS", 50);


        years.add(0, "2016/2017");
        years.add(1, "2017/2018");
        years.add(2, "2018/2019");
        organisationYearsMap.put("Empire Ltd", years);
        organisationYearsMap.put("Ludlow", years);
        organisationYearsMap.put("EGGS", years);

        eligibleCostPerYear.add(0, BigDecimal.valueOf(1000));
        eligibleCostPerYear.add(1, BigDecimal.valueOf(1200));
        eligibleCostPerYear.add(2, BigDecimal.valueOf(1100));
        grantPerYear.add(0, BigDecimal.valueOf(1000));
        grantPerYear.add(1, BigDecimal.valueOf(1200));
        grantPerYear.add(2, BigDecimal.valueOf(1100));
        organisationEligibleCostTotal.put("Empire Ltd", eligibleCostPerYear);
        organisationEligibleCostTotal.put("Ludlow", eligibleCostPerYear);
        organisationEligibleCostTotal.put("EGGS", eligibleCostPerYear);

        organisationGrantAllocationTotal.put("Empire Ltd", grantPerYear);
        organisationGrantAllocationTotal.put("Ludlow", grantPerYear);
        organisationGrantAllocationTotal.put("EGGS", grantPerYear);

        yearEligibleCostTotal.put(years.get(0), BigDecimal.valueOf(3000));
        yearEligibleCostTotal.put(years.get(1), BigDecimal.valueOf(2600));
        yearEligibleCostTotal.put(years.get(2), BigDecimal.valueOf(2300));

        yearGrantAllocationTotal.put(years.get(0), BigDecimal.valueOf(3000));
        yearGrantAllocationTotal.put(years.get(1), BigDecimal.valueOf(2600));
        yearGrantAllocationTotal.put(years.get(2), BigDecimal.valueOf(2300));

        return new YearlyGOLProfileTable(organisationAndGrantPercentageMap, organisationYearsMap,
                organisationEligibleCostTotal, organisationGrantAllocationTotal,
                yearEligibleCostTotal, yearGrantAllocationTotal, eligibleCostGrandTotal, grantAllocationGrandTotal);
    }
}
