package org.innovateuk.ifs.api.filestorage.v1.download;

import org.innovateuk.ifs.api.filestorage.ApiConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface FileDownload {

    @GetMapping(ApiConstants.VERSION_ONE + ApiConstants.PATH + "/stream/{uuid}")
    ResponseEntity<Resource> fileStreamByUuid(@PathVariable("uuid") final String uuid);

    @GetMapping(ApiConstants.VERSION_ONE + ApiConstants.PATH + "/{uuid}")
    ResponseEntity<FileDownloadResponse> fileDownloadResponse(@PathVariable("uuid") final String uuid);

}
