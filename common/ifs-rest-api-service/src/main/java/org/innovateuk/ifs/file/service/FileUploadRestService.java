package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public interface FileUploadRestService {

    RestResult<FileEntryResource> uploadFile(String fileType, String contentType, long contentLength, String originalFilename, byte[] file);
}
