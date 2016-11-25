package com.worth.ifs.project.transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.BasicFileAndContents;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.service.GOLTemplateRenderer;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.gol.YearlyGOLProfileTable;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.util.DateUtil;
import com.worth.ifs.project.util.FinancialYearDate;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.resource.OrganisationResource;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_CONVERT_TO_PDF;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMapValue;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.io.File.separator;
import static java.util.stream.Collectors.toList;

@Service
public class ProjectGrantOfferServiceImpl extends BaseTransactionalService implements ProjectGrantOfferService{

    static final String GOL_TEMPLATES_PATH = "grantoffer" + separator + "grant_offer_letter.html";

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
    private GOLTemplateRenderer golTemplateRenderer;

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
    public ServiceResult<FileEntryResource>  createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)));
    }

    @Override
    public ServiceResult<FileEntryResource> generateGrantOfferLetter(Long projectId, FileEntryResource fileEntryResource) {

        //TODO Implement adding Finance data if approved otherwise skip generation of GOL.
        return getProject(projectId).
                andOnSuccess(project -> golTemplateRenderer.renderTemplate(getTemplatePath(), getTemplateData(project)).
                            andOnSuccess(htmlFile -> convertHtmlToPdf(() -> new ByteArrayInputStream(StringUtils.getBytesUtf8(htmlFile)),
                                    fileEntryResource).
                                    andOnSuccess(inputStreamSupplier ->  fileService.createFile(fileEntryResource, inputStreamSupplier).
                                            andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)))));
    }

    private List<YearlyGOLProfileTable> createSpendProfileSummaryYears(Project project, List<OrganisationResource> organisations, SpendProfileTableResource table){
        Integer startYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate())).getFiscalYear();
        Integer endYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate().plusMonths(project.getDurationInMonths()))).getFiscalYear();

        Map<Long, SpendProfileTableResource> organisationSpendProfiles = organisations.stream().collect(Collectors.toMap(OrganisationResource::getId, organisation -> {

            ProjectOrganisationCompositeId organisationCompositeId = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
            return projectFinanceService.getSpendProfileTable(organisationCompositeId).getSuccessObject();
        }));

        Map<Long, List<BigDecimal>> monthlyCostsPerOrganisationMap = simpleMapValue(organisationSpendProfiles, tableResource -> {
            return calculateMonthlyTotals(tableResource.getMonthlyCostsPerCategoryMap(), tableResource.getMonths().size());
        });

        Map<Long, BigDecimal> eligibleCostPerOrganisationMap = simpleMapValue(organisationSpendProfiles, tableResource -> {
            return calculateTotalOfAllEligibleTotals(tableResource.getEligibleCostPerCategoryMap());
        });

        BigDecimal totalOfAllActualTotals = calculateTotalOfAllActualTotals(monthlyCostsPerOrganisationMap);

        BigDecimal totalOfAllEligibleTotals = calculateTotalOfAllEligibleTotals(eligibleCostPerOrganisationMap);
        return IntStream.range(startYear, endYear + 1).
                mapToObj(
                        year -> {
                            Set<Long> keys = table.getMonthlyCostsPerCategoryMap().keySet();
                            BigDecimal totalForYear = BigDecimal.ZERO;

                            for(Long key : keys){
                                List<BigDecimal> values = table.getMonthlyCostsPerCategoryMap().get(key);
                                for(int i = 0; i < values.size(); i++){
                                    LocalDateResource month = table.getMonths().get(i);
                                    FinancialYearDate financialYearDate = new FinancialYearDate(DateUtil.asDate(month.getLocalDate()));
                                    if(year == financialYearDate.getFiscalYear()){
                                        totalForYear = totalForYear.add(values.get(i));
                                    }
                                }
                            }
                            return new YearlyGOLProfileTable(year, totalForYear.toPlainString(),null,null, null, null );
                        }

                ).collect(toList());
    }

    public BigDecimal calculateTotalOfAllActualTotals(Map<Long, List<BigDecimal>> tableData) {
        return tableData.values()
                .stream()
                .map(list -> {
                    return list.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calculateTotalOfAllEligibleTotals(Map<Long, BigDecimal> eligibleCostData) {
        return eligibleCostData
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<BigDecimal> calculateMonthlyTotals(Map<Long, List<BigDecimal>> tableData, int numberOfMonths) {
        return IntStream.range(0, numberOfMonths).mapToObj(index -> {
            return tableData.values()
                    .stream()
                    .map(list -> list.get(index))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }).collect(toList());
    }


    private Map<String, Object> getTemplateData(Project project) {
        Map<String, Object> templateReplacements = new HashMap<>();
        List<String> addresses = getAddresses(project);
        templateReplacements.put("LeadContact", project.getApplication().getLeadApplicant().getName());
        templateReplacements.put("LeadOrgName", project.getApplication().getLeadApplicant().getName());
        templateReplacements.put("Address1", addresses.size() == 0 ? "" : addresses.get(0));
        templateReplacements.put("Address2", addresses.size() == 0 ? "" : addresses.get(1));
        templateReplacements.put("Address3", addresses.size() == 0 ? "" : addresses.get(2));
        templateReplacements.put("TownCity", addresses.size() == 0 ? "" : addresses.get(3));
        templateReplacements.put("PostCode", addresses.size() == 0 ? "" : addresses.get(4));
        templateReplacements.put("Date", LocalDateTime.now().toString());
        templateReplacements.put("CompetitionName", project.getApplication().getCompetition().getName());
        templateReplacements.put("ProjectTitle", project.getName());
        templateReplacements.put("ProjectStartDate", project.getTargetStartDate() != null ?
                project.getTargetStartDate().toString() : "");
        templateReplacements.put("ProjectLength", project.getDurationInMonths());
        templateReplacements.put("ApplicationNumber", project.getApplication().getId());
        return templateReplacements;
    }

    private List<String> getAddresses(Project project) {
        List<String> addressLines = new ArrayList<>();
        if (project.getAddress() != null ) {
            Address address = project.getAddress();
            addressLines.add(address.getAddressLine1() != null ? address.getAddressLine1() : "" );
            addressLines.add(address.getAddressLine2() != null ? address.getAddressLine2() : "" );
            addressLines.add((address.getAddressLine3() != null ? address.getAddressLine3() : "" ));
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
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT);
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
}
