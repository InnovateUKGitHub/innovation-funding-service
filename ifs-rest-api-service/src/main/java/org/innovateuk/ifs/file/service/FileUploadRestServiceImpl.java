package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class FileUploadRestServiceImpl extends BaseRestService implements FileUploadRestService {

    private String fileUploadRestURL = "/file/file-upload";

    @Override
    public RestResult<FileEntryResource> addFile(Long uploadId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = fileUploadRestURL + "/add-file" +
                "?uploadId=" + uploadId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType, contentLength);
        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFile(Long uploadId) {
        String url = fileUploadRestURL + "/delete-file" +
                "?uploadId=" + uploadId;
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long uploadId) {
        String url = fileUploadRestURL + "/get-file" +
                "?uploadId=" + uploadId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetails(Long uploadId) {
        String url = fileUploadRestURL + "/get-file/fileentry" +
                "?uploadId=" + uploadId;

        return getWithRestResult(url, FileEntryResource.class);
    }
}
