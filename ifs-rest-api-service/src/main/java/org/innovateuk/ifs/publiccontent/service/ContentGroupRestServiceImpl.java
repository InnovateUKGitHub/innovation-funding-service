package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

/**
 * Implementation for public content rest calls.
 */
@Service
public class ContentGroupRestServiceImpl extends BaseRestService implements ContentGroupRestService {

    private static final String CONTENT_GROUP_REST_URL = "/public-group/";

    @Override
    public RestResult<Void> uploadFile(Long groupId, String contentType, long contentLength, String originalFilename, byte[] file) {
        return postWithRestResult(CONTENT_GROUP_REST_URL + "upload-file/" + groupId + "/" + originalFilename,
                file, createFileUploadHeader(contentType,  contentLength), Void.class);
    }

    @Override
    public RestResult<Void> removeFile(Long groupId) {
        return postWithRestResult(CONTENT_GROUP_REST_URL + "remove-file/" + groupId, Void.class);
    }
}
