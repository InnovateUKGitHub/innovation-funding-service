package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.transactional.FinanceFileEntryService;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {

    @Value("${ifs.data.service.file.storage.applicationfinance.max.filesize.bytes}")
    private Long maxFilesizeBytesForApplicationFinance;

    @Value("${ifs.data.service.file.storage.applicationfinance.valid.media.types}")
    private List<String> validMediaTypesForApplicationFinance;

    @Autowired
    private FinanceRowCostsService financeRowCostsService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FinanceFileEntryService financeFileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @GetMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/findByApplication/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return financeService.findApplicationFinanceByApplication(applicationId).toGetResponse();
    }

    @GetMapping("/getResearchParticipationPercentage/{applicationId}")
    public RestResult<Double> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {
        return financeService.getResearchParticipationPercentage(applicationId).toGetResponse();
    }

    @PostMapping("/add/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeRowCostsService.addCost(new ApplicationFinanceResourceId(applicationId, organisationId)).toPostCreateResponse();
    }

    @GetMapping("/getById/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return financeService.getApplicationFinanceById(applicationFinanceId).toGetResponse();
    }

    @PostMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return financeRowCostsService.updateCost(applicationFinanceId, applicationFinance).toPutWithBodyResponse();
    }

    @GetMapping("/financeDetails/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return financeService.financeDetails(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/financeDetails/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeDetails(@PathVariable("applicationId") final Long applicationId) {
        return financeService.financeDetails(applicationId).toGetResponse();
    }

    @GetMapping("/financeTotals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return financeService.financeTotals(applicationId).toGetResponse();
    }

    @PostMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<FileEntryResource> addFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance, request, (fileAttributes, inputStreamSupplier) ->
                financeFileEntryService.createFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PutMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance, request, (fileAttributes, inputStreamSupplier) ->
                financeFileEntryService.updateFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<Void> deleteResult = financeFileEntryService.deleteFinanceFileEntry(applicationFinanceId);
        return deleteResult.toDeleteResponse();
    }

    @GetMapping("/financeDocument")
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        return fileControllerUtils.handleFileDownload(() -> financeFileEntryService.getFileContents(applicationFinanceId));
    }

    @GetMapping("/financeDocument/fileentry")
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {
        return financeFileEntryService.getFileContents(applicationFinanceId).
                andOnSuccessReturn(FileAndContents::getFileEntry).
                toGetResponse();
    }
}
