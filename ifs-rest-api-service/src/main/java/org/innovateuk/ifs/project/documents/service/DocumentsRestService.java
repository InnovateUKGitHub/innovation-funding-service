package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public interface DocumentsRestService {

    RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long fileSize, String originalFilename, byte[] bytes);
}
