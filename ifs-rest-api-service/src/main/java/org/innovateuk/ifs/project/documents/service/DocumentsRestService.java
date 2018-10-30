package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface DocumentsRestService {

    RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Optional<ByteArrayResource>> getFileContents(long projectId, long documentConfigId);

    RestResult<Optional<FileEntryResource>> getFileEntryDetails(long projectId, long documentConfigId);

    RestResult<Void> deleteDocument(long projectId, long documentConfigId);

    RestResult<Void> submitDocument(long projectId, long documentConfigId);
}
