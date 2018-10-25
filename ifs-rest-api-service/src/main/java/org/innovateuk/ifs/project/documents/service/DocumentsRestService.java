package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * REST service for Project Document related operations
 */
public interface DocumentsRestService {

    RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Optional<ByteArrayResource>> getFileContents(long projectId, long documentConfigId);

    RestResult<Optional<FileEntryResource>> getFileEntryDetails(long projectId, long documentConfigId);

    RestResult<Void> deleteDocument(long projectId, long documentConfigId);

    RestResult<Void> submitDocument(long projectId, long documentConfigId);

    RestResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision);
}
