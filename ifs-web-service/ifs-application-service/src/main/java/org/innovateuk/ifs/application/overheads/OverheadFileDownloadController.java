package org.innovateuk.ifs.application.overheads;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Controller will handle requests related to uploading a file to be attached to an overhead question
 */
@Controller
@RequestMapping("/application/download/overheadfile")
@SecuredBySpring(value="Controller", description = "Applicants and internal users can download uploaded overheads spreadsheet", securedType = OverheadFileDownloadController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead')")
public class OverheadFileDownloadController {

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @GetMapping("/{overheadId}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("overheadId") final Long overheadId) {

        final ByteArrayResource resource = overheadFileRestService.getOverheadFile(overheadId).getSuccess();
        final FileEntryResource fileEntryResource = overheadFileRestService.getOverheadFileDetails(overheadId).getSuccess();
        return getFileResponseEntity(resource, fileEntryResource);
    }
}