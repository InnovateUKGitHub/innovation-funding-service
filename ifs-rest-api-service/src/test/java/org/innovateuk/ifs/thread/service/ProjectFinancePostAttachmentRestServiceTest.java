package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.upload.service.ProjectFinancePostAttachmentRestService;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class ProjectFinancePostAttachmentRestServiceTest extends BaseRestServiceUnitTest<ProjectFinancePostAttachmentRestService> {
    private final static String baseURL = "/project/finance/attachment";

    @Override
    protected ProjectFinancePostAttachmentRestService registerRestServiceUnderTest() {
        return new ProjectFinancePostAttachmentRestService();
    }

    @Test
    public void test_find() throws Exception {
        FileEntryResource expected = newFileEntryResource().withId(199L).build();
        setupGetWithRestResultExpectations(baseURL + "/199", FileEntryResource.class, expected, OK);
        final FileEntryResource response = service.find(199L).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void test_upload() throws Exception {
        String fileContentString = "keDFjFGrueurFGy3456efhjdg3";
        byte[] fileContent = fileContentString.getBytes();
        final String originalFilename = "testFile.pdf";
        final String contentType = "text/pdf";
        String url = baseURL + "/upload?filename=" + originalFilename;

        FileEntryResource expected = newFileEntryResource().withId(199L).withFilesizeBytes(fileContent.length)
                .withMediaType(contentType).build();

        setupFileUploadWithRestResultExpectations(url, FileEntryResource.class,
                fileContentString, contentType, fileContent.length, expected, CREATED);

        final FileEntryResource response = service.upload(contentType, fileContent.length, originalFilename, fileContent).getSuccessObject();
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
        final Optional<ByteArrayResource> response = service.download(fileId).getSuccessObject();
        assertSame(expected, response.get());
    }

}
