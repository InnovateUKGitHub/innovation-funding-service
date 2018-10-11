package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentsRestServiceImpl extends BaseRestService implements DocumentsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/document/config/" + documentConfigId + "/upload?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getFileContents(long projectId, long documentConfigId) {
        String url = projectRestURL + "/" + projectId + "/document/config/" + documentConfigId + "/file-contents";
        return getWithRestResult(url, ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getFileEntryDetails(long projectId, long documentConfigId) {
        String url = projectRestURL + "/" + projectId + "/document/config/" + documentConfigId + "/file-entry-details";
        return getWithRestResult(url, FileEntryResource.class).toOptionalIfNotFound();
    }
}

