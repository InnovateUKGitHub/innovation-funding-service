package org.innovateuk.ifs.upload.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
@Qualifier("projectFinance")
public class ProjectFinancePostAttachmentRestService extends BaseRestService implements AttachmentRestService {
    private final static String BASE_URL = "/project/finance/attachments";

    @Override
    public RestResult<AttachmentResource> find(Long fileId) {
        return getWithRestResult(BASE_URL +"/" + fileId, AttachmentResource.class);
    }

    @Override
    public RestResult<AttachmentResource> upload(Long projectId, String contentType, long contentLength,
                                                    String originalFilename, byte[] bytes)
    {
        String url =  BASE_URL + "/" + projectId + "/upload?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), AttachmentResource.class);
    }

    @Override
    public RestResult<Void> delete(Long id) {
        return deleteWithRestResult(BASE_URL + "/" + id);
    }

    @Override
    public RestResult<ByteArrayResource> download(Long fileId) {
        return getWithRestResult(BASE_URL +"/download/" + fileId, ByteArrayResource.class);
    }
}