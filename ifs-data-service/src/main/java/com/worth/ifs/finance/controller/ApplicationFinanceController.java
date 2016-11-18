package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.worth.ifs.file.controller.FileControllerUtils.*;
import static com.worth.ifs.finance.resource.ApplicationFinanceConstants.RESEARCH_PARTICIPATION_PERCENTAGE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This RestController exposes CRUD operations to both the
 * {com.worth.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
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

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeRowService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/findByApplication/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return financeRowService.findApplicationFinanceByApplication(applicationId).toGetResponse();
    }

    // TODO DW - INFUND-1555 - remove ObjectNode usage
    @RequestMapping("/getResearchParticipationPercentage/{applicationId}")
    public RestResult<ObjectNode> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {

        ServiceResult<ObjectNode> result = financeRowService.getResearchParticipationPercentage(applicationId).andOnSuccessReturn(percentage -> {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put(RESEARCH_PARTICIPATION_PERCENTAGE, percentage);
            return node;
        });

        return result.toGetResponse();
    }

    @RequestMapping("/add/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return financeRowService.addCost(new ApplicationFinanceResourceId(applicationId, organisationId)).toPostCreateResponse();
    }

    @RequestMapping("/getById/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return financeRowService.getApplicationFinanceById(applicationFinanceId).toGetResponse();
    }

    @RequestMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return financeRowService.updateCost(applicationFinanceId, applicationFinance).toPutWithBodyResponse();
    }

    @RequestMapping("/financeDetails/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return financeRowService.financeDetails(applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/financeTotals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return financeRowService.financeTotals(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/financeDocument", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                financeRowService.createFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/financeDocument", method = PUT, produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                financeRowService.updateFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/financeDocument", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<Void> deleteResult = financeRowService.deleteFinanceFileEntry(applicationFinanceId);
        return deleteResult.toDeleteResponse();
    }

    @RequestMapping(value = "/financeDocument", method = GET)
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        return handleFileDownload(() -> financeRowService.getFileContents(applicationFinanceId));
    }

    @RequestMapping(value = "/financeDocument/fileentry", method = GET)
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {
        return financeRowService.getFileContents(applicationFinanceId).
                andOnSuccessReturn(FileAndContents::getFileEntry).
                toGetResponse();
    }
}
