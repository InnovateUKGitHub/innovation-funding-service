package org.innovateuk.ifs.documents;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.service.DocumentsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public class DocumentsServiceImpl implements DocumentsService {

    @Autowired
    private DocumentsRestService documentsRestService;

    @Override
    public Optional<ByteArrayResource> getFileContents(long projectId, long documentConfigId) {
        return documentsRestService.getFileContents(projectId, documentConfigId).getSuccess();
    }

    @Override
    public Optional<FileEntryResource> getFileEntryDetails(long projectId, long documentConfigId) {
        return documentsRestService.getFileEntryDetails(projectId, documentConfigId).getSuccess();
    }

    @Override
    public ServiceResult<Void> deleteDocument(long projectId, long documentConfigId) {
        return documentsRestService.deleteDocument(projectId, documentConfigId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision) {
        return documentsRestService.documentDecision(projectId, documentConfigId, decision).toServiceResult();
    }

    @Override
    public ServiceResult<Void> submitDocument(long projectId, long documentConfigId) {
        return documentsRestService.submitDocument(projectId, documentConfigId).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long fileSize, String orignalFilename, byte[] bytes) {
        return documentsRestService.uploadDocument(projectId, documentConfigId, contentType, fileSize, orignalFilename, bytes).toServiceResult();
    }
}
