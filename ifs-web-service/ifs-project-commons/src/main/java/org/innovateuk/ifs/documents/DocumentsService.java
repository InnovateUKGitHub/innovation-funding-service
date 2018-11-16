package org.innovateuk.ifs.documents;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface DocumentsService {

    Optional<ByteArrayResource> getFileContents(long projectId, long documentConfigId);

    Optional<FileEntryResource> getFileEntryDetails(long projectId, long documentConfigId);

    ServiceResult<Void> deleteDocument(long projectId, long documentConfigId);

    ServiceResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision);

    ServiceResult<Void> submitDocument(long projectId, long documentConfigId);

    ServiceResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long fileSize, String orignalFilename, byte[] bytes);
}
