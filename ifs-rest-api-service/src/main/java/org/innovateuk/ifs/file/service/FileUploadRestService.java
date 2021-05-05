package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

public interface FileUploadRestService {

    RestResult<FileEntryResource> addFile(Long uploadId, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFile(Long uploadId);
    RestResult<ByteArrayResource> getFile(Long uploadId);
    RestResult<FileEntryResource> getFileDetails(Long uploadId);
}
