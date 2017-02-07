package org.innovateuk.ifs.upload.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostAttachmentRestServiceImpl extends BaseRestService implements PostAttachmentRestService {
    private final static String baseURL = "/attachment";

    @Override
    public RestResult<FileEntryResource> find(Long fileId) {
        return getWithRestResult(baseURL+"/fileId", FileEntryResource.class);
    }

    @Override
    public RestResult<FileEntryResource> upload(String contentType, long contentLength,
                                                    String originalFilename, byte[] bytes)
    {
        String url =  baseURL + "/upload?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> delete(Long id) {
        return deleteWithRestResult(baseURL + "/" + id);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> download(Long fileId) {
        return getWithRestResult(baseURL+"/download/fileId", ByteArrayResource.class).toOptionalIfNotFound();
    }
}