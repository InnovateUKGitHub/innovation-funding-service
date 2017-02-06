package org.innovateuk.ifs.upload.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public interface FileUploadRestService {

    RestResult<FileEntryResource> uploadFile(Long projectId, String contentType, long contentLength,
                                             String originalFilename, byte[] bytes);

}
