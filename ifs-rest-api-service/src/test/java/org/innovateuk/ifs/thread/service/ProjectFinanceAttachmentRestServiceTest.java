package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.upload.service.ProjectFinancePostAttachmentRestService;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class ProjectFinanceAttachmentRestServiceTest extends BaseRestServiceUnitTest<ProjectFinancePostAttachmentRestService> {
    private final static String baseURL = "/project/finance/attachments";

    @Override
    protected ProjectFinancePostAttachmentRestService registerRestServiceUnderTest() {
        return new ProjectFinancePostAttachmentRestService();
    }

    @Test
    public void test_find() throws Exception {
        AttachmentResource expected = new AttachmentResource(199L, "name", "application/pdf", 1235, null);
        setupGetWithRestResultExpectations(baseURL + "/199", AttachmentResource.class, expected, OK);
        final AttachmentResource response = service.find(199L).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void test_upload() throws Exception {
        String fileContentString = "keDFjFGrueurFGy3456efhjdg3";
        byte[] fileContent = fileContentString.getBytes();
        final String originalFilename = "testFile.pdf";
        final String contentType = "text/pdf";
        final Long projectId = 77L;
        String url = baseURL + "/" + projectId + "/upload?filename=" + originalFilename;

        AttachmentResource expected = new AttachmentResource(199L, "name", "application/pdf", 1235, null);

        setupFileUploadWithRestResultExpectations(url, AttachmentResource.class,
                fileContentString, contentType, fileContent.length, expected, CREATED);

        final AttachmentResource response = service.upload(projectId, contentType, fileContent.length, originalFilename, fileContent).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void test_delete() throws Exception {
        Long fileId = 78L;
        setupDeleteWithRestResultExpectations(baseURL + "/" + fileId);
        service.delete(fileId);
        setupDeleteWithRestResultVerifications(baseURL + "/" + fileId);
    }

    @Test
    public void test_download() throws Exception {
        final Long fileId = 912L;
        ByteArrayResource expected = new ByteArrayResource("1u6536748".getBytes());
        setupGetWithRestResultExpectations(baseURL + "/download/" + fileId, ByteArrayResource.class, expected, OK);
        final ByteArrayResource response = service.download(fileId).getSuccessObject();
        assertSame(expected, response);
    }

}
