package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.transactional.OverheadFileService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller for handling the overheads calculation spreadsheet file
 */
@RestController
@RequestMapping("/overheadcalculation")
public class OverheadFileController {

    @Autowired
    private OverheadFileService overheadFileService;

    @Autowired
    @Qualifier("overheadCalculationFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @RequestMapping(value = "/overheadCalculationDocumentDetails", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getFileDetails(
            @RequestParam(value = "overheadId") long overheadId) {

        return overheadFileService.getFileEntryDetails(overheadId).toGetResponse();
    }

    @RequestMapping(value = "/overheadCalculationDocument", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getFileContents(
            @RequestParam("overheadId") long overheadId) throws IOException {

        return handleFileDownload(() -> overheadFileService.getFileEntryContents(overheadId));
    }

    @RequestMapping(value = "/overheadCalculationDocument", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> createCalculationFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "overheadId") long overheadId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                overheadFileService.createFileEntry(overheadId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/overheadCalculationDocument", method = PUT, produces = "application/json")
    public RestResult<FileEntryResource> updateCalculationFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "overheadId") long overheadId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                overheadFileService.updateFileEntry(overheadId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/overheadCalculationDocument", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteCalculationFile(
            @RequestParam(value = "overheadId") long overheadId) throws IOException {

        return overheadFileService.deleteFileEntry(overheadId).toGetResponse();
    }
}