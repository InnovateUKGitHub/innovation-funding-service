package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpdate;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;

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


    @GetMapping("/{projectId}/signed-grant-offer")
    public @ResponseBody ResponseEntity<Object> getGrantOfferLetterFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getSignedGrantOfferLetterFileAndContents(projectId));
    }

    @GetMapping("/{projectId}/grant-offer")
    public @ResponseBody ResponseEntity<Object> getGeneratedGrantOfferLetterFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getGrantOfferLetterFileAndContents(projectId));
    }


    @GetMapping("/{projectId}/additional-contract")
    public @ResponseBody ResponseEntity<Object> getAdditionalContractFileContents(
            @PathVariable("projectId") long projectId) throws IOException {
        return handleFileDownload(() -> projectGrantOfferService.getAdditionalContractFileAndContents(projectId));
    }


    @GetMapping(value = "/{projectId}/signed-grant-offer/details", produces = "application/json")
    public RestResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getSignedGrantOfferLetterFileEntryDetails(projectId).toGetResponse();
    }

    @GetMapping(value = "/{projectId}/grant-offer/details", produces = "application/json")
    public RestResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getGrantOfferLetterFileEntryDetails(projectId).toGetResponse();
    }


    @GetMapping(value = "/{projectId}/additional-contract/details", produces = "application/json")
    public RestResult<FileEntryResource> getAdditionalContractFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectGrantOfferService.getAdditionalContractFileEntryDetails(projectId).toGetResponse();
    }


    @PostMapping(value = "/{projectId}/signed-grant-offer", produces = "application/json")
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

    @PostMapping(value = "/{projectId}/grant-offer", produces = "application/json")
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

    @DeleteMapping(value = "/{projectId}/grant-offer", produces = "application/json")
    public RestResult<Void> removeGrantOfferLetterFile(@PathVariable(value = "projectId") long projectId) {

        return projectGrantOfferService.removeGrantOfferLetterFileEntry(projectId).toDeleteResponse();
    }

    @DeleteMapping(value = "/{projectId}/signed-grant-offer-letter", produces = "application/json")
    public RestResult<Void> removeSignedGrantOfferLetterFile(@PathVariable(value = "projectId") long projectId) {

        return projectGrantOfferService.removeSignedGrantOfferLetterFileEntry(projectId).toDeleteResponse();
    }

    @PostMapping(value = "/{projectId}/additional-contract", produces = "application/json")
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

    @PutMapping(value = "/{projectId}/signed-grant-offer", produces = "application/json")
    public RestResult<Void> updateGrantOfferLetterFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectGrantOfferService.updateSignedGrantOfferLetterFile(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PostMapping("/{projectId}/grant-offer/submit")
    public RestResult<Void> submitGrantOfferLetter(@PathVariable("projectId") long projectId) {
        return projectGrantOfferService.submitGrantOfferLetter(projectId).toPostResponse();
    }
}
