
package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class ContentGroupRestServiceMocksTest extends BaseRestServiceUnitTest<ContentGroupRestServiceImpl> {

    private static final String CONTENT_GROUP_REST_URL = "/content-group/";


    @Override
    protected ContentGroupRestServiceImpl registerRestServiceUnderTest() {
        ContentGroupRestServiceImpl contentGroupRestServiceImpl = new ContentGroupRestServiceImpl();
        return contentGroupRestServiceImpl;
    }

    @Test
    public void test_uploadFile() {
        Long groupId = 1L;
        String contentType = "text/plain";
        long contentLength= 1000L;
        String originalFilename = "original.pdf";
        String content = "New content";

        String expectedUrl = CONTENT_GROUP_REST_URL + "upload-file?contentGroupId=" + groupId + "&filename=" + originalFilename;

        setupFileUploadWithRestResultExpectations(
                expectedUrl, content, contentType, contentLength, OK);

        service.uploadFile(groupId, contentType, contentLength, originalFilename, content.getBytes()).getSuccessObjectOrThrowException();

    }

    @Test
    public void test_removeFile() {
        Long groupId = 1L;
        String expectedUrl = CONTENT_GROUP_REST_URL + "remove-file/" + groupId;

        setupPostWithRestResultExpectations(expectedUrl, OK);

        service.removeFile(groupId).getSuccessObjectOrThrowException();

    }

    @Test
    public void test_getFile() {
        Long groupId = 1L;
        String expectedUrl = CONTENT_GROUP_REST_URL + "get-file-contents/" + groupId;

        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());
        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource actual = service.getFile(groupId).getSuccessObjectOrThrowException();

        assertThat(actual, equalTo(returnedFileContents));

    }

    @Test
    public void test_getFileDetails() {
        Long groupId = 1L;
        String expectedUrl = CONTENT_GROUP_REST_URL + "get-file-details/" + groupId;

        FileEntryResource returnedFileEntry = new FileEntryResource();
        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        FileEntryResource actual =  service.getFileDetails(groupId).getSuccessObjectOrThrowException();

        assertThat(actual, equalTo(returnedFileEntry));
    }

    @Test
    public void test_getFileAnonymous() {
        Long groupId = 1L;
        String expectedUrl = CONTENT_GROUP_REST_URL + "get-file-contents/" + groupId;

        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());
        setupGetWithRestResultAnonymousExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource actual = service.getFileAnonymous(groupId).getSuccessObjectOrThrowException();

        assertThat(actual, equalTo(returnedFileContents));

    }

    @Test
    public void test_getFileDetailsAnonymous() {
        Long groupId = 1L;
        String expectedUrl = CONTENT_GROUP_REST_URL + "get-file-details/" + groupId;

        FileEntryResource returnedFileEntry = new FileEntryResource();
        setupGetWithRestResultAnonymousExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        FileEntryResource actual =  service.getFileDetailsAnonymous(groupId).getSuccessObjectOrThrowException();

        assertThat(actual, equalTo(returnedFileEntry));
    }

}