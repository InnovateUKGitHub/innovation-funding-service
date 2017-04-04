package org.innovateuk.ifs.application;

import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Controller will handle requests related to uploading a file to be attached to an overhead question
 */
@Controller
@RequestMapping("/application/download/overheadfile")
@PreAuthorize("hasAuthority('applicant')")
public class OverheadFileDownloadController {
    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @GetMapping("/{overheadId}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("overheadId") final Long overheadId,
            HttpServletRequest request) throws ExecutionException, InterruptedException {

        final ByteArrayResource resource = overheadFileRestService.getOverheadFile(overheadId).getSuccessObjectOrThrowException();
        final FileEntryResource fileEntryResource = overheadFileRestService.getOverheadFileDetails(overheadId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileEntryResource);
    }
}