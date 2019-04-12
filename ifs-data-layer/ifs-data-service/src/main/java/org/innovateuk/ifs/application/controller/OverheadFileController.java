package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.finance.transactional.OverheadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for handling the overheads calculation spreadsheet file upload
 */
@RestController
@RequestMapping("/overheadcalculation")
public class OverheadFileController {

    @Value("${ifs.data.service.file.storage.overheadcalculation.max.filesize.bytes}")
    private Long maxFilesizeBytesForOverheadCalculation;

    @Value("${ifs.data.service.file.storage.overheadcalculation.valid.media.types}")
    private List<String> validMediaTypesForOverheadCalculation;

    @Autowired
    private OverheadFileService overheadFileService;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping(value = {"/overheadCalculationDocumentDetails", "/overhead-calculation-document-details"}, produces = "application/json")
    public RestResult<FileEntryResource> getFileDetails(
            @RequestParam(value = "overheadId") long overheadId) {

        return overheadFileService.getFileEntryDetails(overheadId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping(value = {"/projectOverheadCalculationDocumentDetails", "/project-overhead-calculation-document-details"}, produces = "application/json")
    public RestResult<FileEntryResource> getProjectFileDetails(
            @RequestParam(value = "overheadId") long overheadId) {

        return overheadFileService.getProjectFileEntryDetails(overheadId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/overheadCalculationDocument", "/overhead-calculation-document"})
    public @ResponseBody
    ResponseEntity<Object> getFileContents(
            @RequestParam("overheadId") long overheadId) {

        return fileControllerUtils.handleFileDownload(() -> overheadFileService.getFileEntryContents(overheadId));
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/projectOverheadCalculationDocument", "/project-overhead-calculation-document"})
    public @ResponseBody
    ResponseEntity<Object> getProjectFileContents(
            @RequestParam("overheadId") long overheadId) {
        return fileControllerUtils.handleFileDownload(() -> overheadFileService.getProjectFileEntryContents(overheadId));
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping(value = {"/overheadCalculationDocument", "/overhead-calculation-document"}, produces = "application/json")
    public RestResult<FileEntryResource> createCalculationFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "overheadId") long overheadId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForOverheadCalculation, maxFilesizeBytesForOverheadCalculation, request, (fileAttributes, inputStreamSupplier) ->
                overheadFileService.createFileEntry(overheadId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PutMapping(value = {"/overheadCalculationDocument", "/overhead-calculation-document"}, produces = "application/json")
    public RestResult<FileEntryResource> updateCalculationFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "overheadId") long overheadId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForOverheadCalculation, maxFilesizeBytesForOverheadCalculation, request, (fileAttributes, inputStreamSupplier) ->
                overheadFileService.updateFileEntry(overheadId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @DeleteMapping(value = {"/overheadCalculationDocument", "/overhead-calculation-document"}, produces = "application/json")
    public RestResult<Void> deleteCalculationFile(
            @RequestParam(value = "overheadId") long overheadId) {

        return overheadFileService.deleteFileEntry(overheadId).toGetResponse();
    }
}