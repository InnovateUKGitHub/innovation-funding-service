package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.project.transactional.ProjectGrantOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.worth.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static com.worth.ifs.file.controller.FileControllerUtils.handleFileUpdate;
import static com.worth.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Project Controller extension for grant offer data and operations through a REST API
 **/
@RestController
@RequestMapping("/project")
public class ProjectGrantOfferController {

    @Autowired
    private ProjectGrantOfferService projectGrantOfferService;

    @Autowired
    @Qualifier("projectSetupGrantOfferLetterFileValidator")
    private FileHttpHeadersValidator fileValidator;


    @RequestMapping(value = "/{projectId}/signed-grant-offer", method = GET)
    public @ResponseBody ResponseEntity<Object> getGrantOfferLetterFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getSignedGrantOfferLetterFileAndContents(projectId));
    }

    @RequestMapping(value = "/{projectId}/grant-offer", method = GET)
    public @ResponseBody ResponseEntity<Object> getGeneratedGrantOfferLetterFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getGrantOfferLetterFileAndContents(projectId));
    }


    @RequestMapping(value = "/{projectId}/additional-contract", method = GET)
    public @ResponseBody ResponseEntity<Object> getAdditionalContractFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getAdditionalContractFileAndContents(projectId));
    }


    @RequestMapping(value = "/{projectId}/signed-grant-offer/details", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getSignedGrantOfferLetterFileEntryDetails(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/grant-offer/details", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getGrantOfferLetterFileEntryDetails(projectId).toGetResponse();
    }


    @RequestMapping(value = "/{projectId}/additional-contract/details", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getAdditionalContractFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getAdditionalContractFileEntryDetails(projectId).toGetResponse();
    }


    @RequestMapping(value = "/{projectId}/signed-grant-offer", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addSignedGrantOfferLetterFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectGrantOfferService.createSignedGrantOfferLetterFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier)
        );
    }

    @RequestMapping(value = "/{projectId}/grant-offer", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addGrantOfferLetterFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectGrantOfferService.createGrantOfferLetterFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier)
        );
    }


    @RequestMapping(value = "/{projectId}/additional-contract", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addAdditionalContractFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectGrantOfferService.createAdditionalContractFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier)
        );
    }

    @RequestMapping(value = "/{projectId}/signed-grant-offer", method = PUT, produces = "application/json")
    public RestResult<Void> updateGrantOfferLetterFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectGrantOfferService.updateSignedGrantOfferLetterFile(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/{projectId}/grant-offer/submit", method = POST)
    public RestResult<Void> submitGrantOfferLetter(@PathVariable("projectId") long projectId) {
        return projectGrantOfferService.submitGrantOfferLetter(projectId).toPostResponse();
    }
}
