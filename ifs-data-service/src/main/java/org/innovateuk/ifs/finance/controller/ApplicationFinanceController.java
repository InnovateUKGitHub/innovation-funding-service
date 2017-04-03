package org.innovateuk.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.*;
import static org.innovateuk.ifs.finance.resource.ApplicationFinanceConstants.RESEARCH_PARTICIPATION_PERCENTAGE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    @Qualifier("applicationFinanceFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @GetMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeRowService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/findByApplication/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return financeRowService.findApplicationFinanceByApplication(applicationId).toGetResponse();
    }

    // TODO DW - INFUND-1555 - remove ObjectNode usage
    @GetMapping("/getResearchParticipationPercentage/{applicationId}")
    public RestResult<ObjectNode> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {

        ServiceResult<ObjectNode> result = financeRowService.getResearchParticipationPercentage(applicationId).andOnSuccessReturn(percentage -> {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put(RESEARCH_PARTICIPATION_PERCENTAGE, percentage);
            return node;
        });

        return result.toGetResponse();
    }

    @PostMapping("/add/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeRowService.addCost(new ApplicationFinanceResourceId(applicationId, organisationId)).toPostCreateResponse();
    }

    @GetMapping("/getById/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return financeRowService.getApplicationFinanceById(applicationFinanceId).toGetResponse();
    }

    @PostMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return financeRowService.updateCost(applicationFinanceId, applicationFinance).toPutWithBodyResponse();
    }

    @GetMapping("/financeDetails/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return financeRowService.financeDetails(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/financeDetails/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeDetails(@PathVariable("applicationId") final Long applicationId) {
        return financeRowService.financeDetails(applicationId).toGetResponse();
    }

    @GetMapping("/financeTotals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return financeRowService.financeTotals(applicationId).toGetResponse();
    }

    @PostMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<FileEntryResource> addFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                financeRowService.createFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PutMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                financeRowService.updateFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/financeDocument", produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<Void> deleteResult = financeRowService.deleteFinanceFileEntry(applicationFinanceId);
        return deleteResult.toDeleteResponse();
    }

    @GetMapping("/financeDocument")
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        return handleFileDownload(() -> financeRowService.getFileContents(applicationFinanceId));
    }

    @GetMapping("/financeDocument/fileentry")
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {
        return financeRowService.getFileContents(applicationFinanceId).
                andOnSuccessReturn(FileAndContents::getFileEntry).
                toGetResponse();
    }
}
