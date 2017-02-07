package org.innovateuk.ifs.upload.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public class FileUploadRestServiceImpl extends BaseRestService implements FileUploadRestService {
    private final static String baseURL = "/upload";

    @Override
    public RestResult<FileEntryResource> uploadFile(String contentType, long contentLength,
                                                    String originalFilename, byte[] bytes)
    {
        String url =  baseURL + "/?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> deleteFile(Long id) {
        return deleteWithRestResult(baseURL + "/" + id);
    }
}