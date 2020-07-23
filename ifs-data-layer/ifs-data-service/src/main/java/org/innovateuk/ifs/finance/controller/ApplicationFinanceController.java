package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.FinanceFileEntryService;
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
    private ApplicationFinanceService financeService;

    @Autowired
    private FinanceFileEntryService financeFileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @GetMapping("/find-by-application-organisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/find-by-application/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return financeService.findApplicationFinanceByApplication(applicationId).toGetResponse();
    }

    @GetMapping("/get-research-participation-percentage/{applicationId}")
    public RestResult<Double> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {
        return financeService.getResearchParticipationPercentage(applicationId).toGetResponse();
    }

    @GetMapping("/get-by-id/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return financeService.getApplicationFinanceById(applicationFinanceId).toGetResponse();
    }

    @PostMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return financeService.updateApplicationFinance(applicationFinanceId, applicationFinance).toPostWithBodyResponse();
    }

    @GetMapping("/finance-details/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return financeService.financeDetails(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/finance-details/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeDetails(@PathVariable("applicationId") final Long applicationId) {
        return financeService.financeDetails(applicationId).toGetResponse();
    }

    @GetMapping("/finance-totals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return financeService.financeTotals(applicationId).toGetResponse();
    }

    @PostMapping(value = "/finance-document", produces = "application/json")
    public RestResult<FileEntryResource> addFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance, request, (fileAttributes, inputStreamSupplier) ->
                financeFileEntryService.createFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PutMapping(value = "/finance-document", produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance, request, (fileAttributes, inputStreamSupplier) ->
                financeFileEntryService.updateFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/finance-document", produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<Void> deleteResult = financeFileEntryService.deleteFinanceFileEntry(applicationFinanceId);
        return deleteResult.toDeleteResponse();
    }

    @GetMapping("/finance-document")
    public @ResponseBody
    ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        return fileControllerUtils.handleFileDownload(() -> financeFileEntryService.getFileContents(applicationFinanceId));
    }

    @GetMapping("/finance-document/fileentry")
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {
        return financeFileEntryService.getFileContents(applicationFinanceId).
                andOnSuccessReturn(FileAndContents::getFileEntry).
                toGetResponse();
    }
}
