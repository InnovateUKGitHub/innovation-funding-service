package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
/**
 * REST service implementation for Project Document related operations
 */
public class DocumentsRestServiceImpl extends BaseRestService implements DocumentsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = String.format("%s/%s/document/config/%s/upload?filename=%s", projectRestURL, projectId, documentConfigId, originalFilename);
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getFileContents(long projectId, long documentConfigId) {
        String url = String.format("%s/%s/document/config/%s/file-contents", projectRestURL, projectId, documentConfigId);
        return getWithRestResult(url, ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getFileEntryDetails(long projectId, long documentConfigId) {
        String url = String.format("%s/%s/document/config/%s/file-entry-details", projectRestURL, projectId, documentConfigId);
        return getWithRestResult(url, FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Void> deleteDocument(long projectId, long documentConfigId) {
        String url = String.format("%s/%s/document/config/%s/delete", projectRestURL, projectId, documentConfigId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<Void> submitDocument(long projectId, long documentConfigId) {
        String url = String.format("%s/%s/document/config/%s/submit", projectRestURL, projectId, documentConfigId);
        return postWithRestResult(url);
    }

    @Override
    public RestResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision) {
        String url = String.format("%s/%s/document/config/%s/decision", projectRestURL, projectId, documentConfigId);
        return postWithRestResult(url, decision, Void.class);
    }
}

