package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

@RestController
@RequestMapping("/application/download/overheadfile")
@SecuredBySpring(value = "Controller", description = "IFS Admin and Project Finance can download uploaded overhead spreadsheet", securedType = OverheadFileDownloaderController.class)
@PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance')")
public class OverheadFileDownloaderController {
    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @GetMapping(value = "/{overheadId}")
    public ResponseEntity<ByteArrayResource> downloadOverheadFile(
            @PathVariable("overheadId") final Long overheadId) throws ExecutionException, InterruptedException {
        final ByteArrayResource resource = overheadFileRestService.getOverheadFileUsingProjectFinanceRowId(overheadId).getSuccess();
        final FileEntryResource fileEntryResource = overheadFileRestService.getOverheadFileDetailsUsingProjectFinanceRowId(overheadId).getSuccess();
        return getFileResponseEntity(resource, fileEntryResource);
    }
}