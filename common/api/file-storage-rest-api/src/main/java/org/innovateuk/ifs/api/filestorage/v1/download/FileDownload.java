package org.innovateuk.ifs.api.filestorage.v1.download;

import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface FileDownload {

    @GetMapping(ApiVersion.VERSION_ONE + "/download/{uuid}")
    ResponseEntity<Object> fileByUuid(@PathVariable("uuid") final String uuid);

}
