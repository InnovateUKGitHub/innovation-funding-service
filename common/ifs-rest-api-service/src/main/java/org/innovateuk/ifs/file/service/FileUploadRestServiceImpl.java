package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class FileUploadRestServiceImpl extends BaseRestService implements FileUploadRestService {

    private String fileUploadRestURL = "/external-system-files";

    @Override
    public RestResult<FileEntryResource> uploadFile(String fileType, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = fileUploadRestURL + "/upload-file" +
                "?fileType=" + fileType +
                "&fileName=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType, contentLength);
        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }
}
