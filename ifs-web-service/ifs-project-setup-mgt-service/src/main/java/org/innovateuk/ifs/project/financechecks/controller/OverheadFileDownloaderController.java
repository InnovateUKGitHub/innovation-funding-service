package org.innovateuk.ifs.project.financechecks.controller;

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

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/download/overheadfile")
@PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
public  class OverheadFileDownloaderController {
    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @GetMapping(value = "/{overheadId}")
    public ResponseEntity<ByteArrayResource> downloadOverheadFile(
            @PathVariable("overheadId") final Long overheadId,
            HttpServletRequest request) throws ExecutionException, InterruptedException {

        final ByteArrayResource resource = overheadFileRestService.getOverheadFile(overheadId).getSuccessObjectOrThrowException();
        final FileEntryResource fileEntryResource = overheadFileRestService.getOverheadFileDetails(overheadId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileEntryResource);
    }

    public static ResponseEntity<ByteArrayResource> getFileResponseEntity(ByteArrayResource resource, FileEntryResource fileEntry) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(resource.contentLength());
        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
        if (StringUtils.hasText(fileEntry.getName())) {
            httpHeaders.add("Content-Disposition", "inline; filename=\"" + fileEntry.getName() + "\"");
        }
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

}