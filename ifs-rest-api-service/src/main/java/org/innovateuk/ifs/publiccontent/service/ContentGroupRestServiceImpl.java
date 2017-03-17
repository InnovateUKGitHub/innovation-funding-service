package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

/**
 * Implementation for public content rest calls.
 */
@Service
public class ContentGroupRestServiceImpl extends BaseRestService implements ContentGroupRestService {

    private static final String CONTENT_GROUP_REST_URL = "/content-group/";

    @Override
    public RestResult<Void> uploadFile(Long groupId, String contentType, long contentLength, String originalFilename, byte[] file) {
        return postWithRestResult(CONTENT_GROUP_REST_URL + "upload-file?contentGroupId=" + groupId + "&filename=" + originalFilename,
                file, createFileUploadHeader(contentType,  contentLength), Void.class);
    }

    @Override
    public RestResult<Void> removeFile(Long groupId) {
        return postWithRestResult(CONTENT_GROUP_REST_URL + "remove-file/" + groupId, Void.class);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long contentGroupId) {
        return getWithRestResult(CONTENT_GROUP_REST_URL + "get-file-contents/" + contentGroupId, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetails(Long contentGroupId) {
        return getWithRestResult(CONTENT_GROUP_REST_URL + "get-file-details/" + contentGroupId, FileEntryResource.class);
    }

    @Override
    public RestResult<ByteArrayResource> getFileAnonymous(Long contentGroupId) {
        return getWithRestResultAnonymous(CONTENT_GROUP_REST_URL + "get-file-contents/" + contentGroupId, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetailsAnonymous(Long contentGroupId) {
        return getWithRestResultAnonymous(CONTENT_GROUP_REST_URL + "get-file-details/" + contentGroupId, FileEntryResource.class);
    }

}
