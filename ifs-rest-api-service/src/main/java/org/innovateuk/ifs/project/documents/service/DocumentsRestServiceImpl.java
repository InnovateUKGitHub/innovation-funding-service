package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.stereotype.Service;

@Service
public class DocumentsRestServiceImpl extends BaseRestService implements DocumentsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<FileEntryResource> uploadDocument(long projectId, long documentConfigId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/document/config/" + documentConfigId + "/upload?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }
}

