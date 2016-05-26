package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.transactional.CostService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.file.controller.FileUploadControllerUtils.inputStreamSupplier;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {

    private static final Log LOG = LogFactory.getLog(ApplicationFinanceController.class);

    public static final String RESEARCH_PARTICIPATION_PERCENTAGE = "researchParticipationPercentage";

    @Autowired
    private CostService costService;

    @Autowired
    @Qualifier("applicationFinanceFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return costService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/findByApplication/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return costService.findApplicationFinanceByApplication(applicationId).toGetResponse();
    }

    // TODO DW - INFUND-1555 - remove ObjectNode usage
    @RequestMapping("/getResearchParticipationPercentage/{applicationId}")
    public RestResult<ObjectNode> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {

        ServiceResult<ObjectNode> result = costService.getResearchParticipationPercentage(applicationId).andOnSuccessReturn(percentage -> {
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

        return costService.addCost(new ApplicationFinanceResourceId(applicationId, organisationId)).toPostCreateResponse();
    }

    @RequestMapping("/getById/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return costService.getApplicationFinanceById(applicationFinanceId).toGetResponse();
    }

    @RequestMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return costService.updateCost(applicationFinanceId, applicationFinance).toPutWithBodyResponse();
    }

    @RequestMapping("/financeDetails/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return costService.financeDetails(applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/financeTotals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return costService.financeTotals(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/financeDocument", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> fileAddedResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                costService.createFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return fileAddedResult.toPostCreateResponse();
    }

    @RequestMapping(value = "/financeDocument", method = PUT, produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> updateResult = fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                costService.updateFinanceFileEntry(applicationFinanceId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/financeDocument", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<Void> deleteResult = costService.deleteFinanceFileEntry(applicationFinanceId);
        return deleteResult.toDeleteResponse();
    }

    @RequestMapping(value = "/financeDocument", method = GET)
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter or RestResult<ByteArrayResource>
        try {
            return costService.getFileContents(applicationFinanceId).handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        FileEntryResource fileEntry = success.getKey();
                        InputStream inputStream = success.getValue().get();
                        ByteArrayResource inputStreamResource = new ByteArrayResource(StreamUtils.copyToByteArray(inputStream));
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );
        } catch (Exception e) {
            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/financeDocument/fileentry", method = GET)
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {
        return costService.getFileContents(applicationFinanceId).
                andOnSuccessReturn(Pair::getKey).
                toGetResponse();
    }
}
