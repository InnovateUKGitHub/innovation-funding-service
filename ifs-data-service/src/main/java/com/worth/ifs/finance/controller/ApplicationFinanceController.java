package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileEntryService;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.file.transactional.FileValidator;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
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
    private FileEntryService fileEntryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper mapper;

    @Autowired
    @Qualifier("applicationFinanceFileValidator")
    private FileValidator fileValidator;

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
                fileValidator.validateFileHeaders(contentLength, contentType, originalFilename).andOnSuccess(fileAttributes ->
                fileService.createFile(new FileEntryResource(null, fileAttributes), inputStreamSupplier(request)).
                andOnSuccessReturn(result -> mapper.mapToResource(result.getValue())));

        fileAddedResult.andOnSuccess(file -> {
            ApplicationFinanceResource applicationFinanceResource = costService.getApplicationFinanceById(applicationFinanceId).getSuccessObject();
            if(applicationFinanceResource!=null) {
                applicationFinanceResource.setFinanceFileEntry(file.getId());
                costService.updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
            }
            return serviceSuccess();
        });

        return fileAddedResult.toPostCreateResponse();
    }

    @RequestMapping(value = "/financeDocument", method = PUT, produces = "application/json")
    public RestResult<Void> updateFinanceDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationFinanceId") long applicationFinanceId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<Void> updateResult = fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes -> {

            ApplicationFinanceResource applicationFinanceResource = costService.getApplicationFinanceById(applicationFinanceId).getSuccessObject();

            if (applicationFinanceResource != null) {

                FileEntryResource fileEntryResource = fileEntryService.findOne(applicationFinanceResource.getFinanceFileEntry()).getSuccessObject();
                if(fileEntryResource!=null) {
                    ServiceResult<Pair<File, FileEntry>> fileEntryResult = fileService.updateFile(fileEntryResource, inputStreamSupplier(request));
                    return fileEntryResult.andOnSuccessReturnVoid();
                }
            }
            return serviceSuccess();
        });

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/financeDocument", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFinanceDocument(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        ServiceResult<ApplicationFinanceResource> applicationFinanceServiceResult = costService.getApplicationFinanceById(applicationFinanceId);
        ServiceResult<ApplicationFinanceResource> applicationFinanceResourceServiceResult = applicationFinanceServiceResult.andOnSuccess(applicationFinanceResource -> {
            Long fileEntryId = applicationFinanceResource.getFinanceFileEntry();
            return fileService.deleteFile(fileEntryId).andOnSuccess(() -> {
                applicationFinanceResource.setFinanceFileEntry(null);
                return costService.updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
            });
        });
        return applicationFinanceResourceServiceResult.toDeleteResponse();
    }

    @RequestMapping(value = "/financeDocument", method = GET)
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("applicationFinanceId") long applicationFinanceId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {
            FileEntryResource fileEntry = doGetFile(applicationFinanceId).getSuccessObject();
            return fileService.getFileByFileEntryId(fileEntry.getId()).handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        InputStream inputStream = success.get();
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

    private ServiceResult<FileEntryResource> doGetFile(long applicationFinanceId) {
        return fileEntryService.getFileEntryByApplicationFinanceId(applicationFinanceId);
    }

    private Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> {
            try {
                return request.getInputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
        };
    }
}
