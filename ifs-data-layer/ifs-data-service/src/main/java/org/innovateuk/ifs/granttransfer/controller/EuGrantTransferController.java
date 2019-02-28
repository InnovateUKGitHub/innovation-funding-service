package org.innovateuk.ifs.granttransfer.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.transactional.EuGrantTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/eu-grant-transfer")
public class EuGrantTransferController {

    @Autowired
    private EuGrantTransferService euGrantTransferService;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @GetMapping(value = "{applicationId}", produces = "application/json")
    public RestResult<EuGrantTransferResource> getGrantTransferByApplicationId(@PathVariable("applicationId") long applicationId) {
        return euGrantTransferService.getGrantTransferByApplicationId(applicationId).toGetResponse();
    }

    @PostMapping(value = "{applicationId}", produces = "application/json")
    public RestResult<Void> getGrantTransferByApplicationId(@PathVariable("applicationId") long applicationId,
                                                                               @RequestBody EuGrantTransferResource euGrantTransferResource) {
        return euGrantTransferService.updateGrantTransferByApplicationId(euGrantTransferResource, applicationId).toPostAcceptResponse();
    }

    @PostMapping(value = "/grant-agreement/{applicationId}", produces = "application/json")
    public RestResult<Void> uploadGrantAgreement(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                           @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                           @RequestParam(value = "filename", required = false) String originalFilename,
                                           @PathVariable("applicationId") long applicationId,
                                           HttpServletRequest request)
    {
        return euGrantTransferService.uploadGrantAgreement(contentType, contentLength, originalFilename, applicationId, request).toPostCreateResponse();
    }

    @DeleteMapping(value = "/grant-agreement/{applicationId}", produces = "application/json")
    public RestResult<Void> deleteGrantAgreement(@PathVariable("applicationId") long applicationId) {
        return euGrantTransferService.deleteGrantAgreement(applicationId).toDeleteResponse();
    }

    @GetMapping(value = "/grant-agreement/{applicationId}", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Object> downloadGrantAgreement(@PathVariable("applicationId") long applicationId) {
        return fileControllerUtils.handleFileDownload(() -> euGrantTransferService.downloadGrantAgreement(applicationId));
    }

    @GetMapping(value = "/grant-agreement-details/{applicationId}", produces = "application/json")
    public RestResult<FileEntryResource> findGrantAgreement(@PathVariable("applicationId") long applicationId) {
        return euGrantTransferService.findGrantAgreement(applicationId).toGetResponse();
    }

}
