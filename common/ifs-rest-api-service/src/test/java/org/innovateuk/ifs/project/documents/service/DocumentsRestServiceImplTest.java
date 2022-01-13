package org.innovateuk.ifs.project.documents.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class DocumentsRestServiceImplTest extends BaseRestServiceUnitTest<DocumentsRestServiceImpl> {
    private static final String projectRestURL = "/project";
    private long projectId = 1L;
    private long documentConfigId = 2L;

    @Override
    protected DocumentsRestServiceImpl registerRestServiceUnderTest() {
        DocumentsRestServiceImpl documentsRestService = new DocumentsRestServiceImpl();
        ReflectionTestUtils.setField(documentsRestService, "projectRestURL", projectRestURL);
        return documentsRestService;
    }

    @Test
    public void uploadDocument() {

        String fileName = "filename.txt";
        String url = String.format("%s/%s/document/config/%s/upload?filename=%s", projectRestURL, projectId, documentConfigId, fileName);

        String fileContentString = "12345678901234567";
        byte[] fileContent = fileContentString.getBytes();
        FileEntryResource response = new FileEntryResource();

        setupFileUploadWithRestResultExpectations(url, FileEntryResource.class,
                fileContentString, "text/plain", 17, response, CREATED);

        RestResult<FileEntryResource> result =
                service.uploadDocument(projectId, documentConfigId, "text/plain", 17, fileName, fileContent);

        assertTrue(result.isSuccess());
        assertEquals(response, result.getSuccess());
    }

    @Test
    public void getFileContents() {

        String url = String.format("%s/%s/document/config/%s/file-contents", projectRestURL, projectId, documentConfigId);
        ByteArrayResource expectedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(url, ByteArrayResource.class, expectedFileContents, OK);

        ByteArrayResource retrievedFileContents = service.getFileContents(projectId, documentConfigId).getSuccess().get();

        assertEquals(expectedFileContents, retrievedFileContents);
    }

    @Test
    public void getFileEntryDetails() {

        String url = String.format("%s/%s/document/config/%s/file-entry-details", projectRestURL, projectId, documentConfigId);
        FileEntryResource expectedFileEntry = new FileEntryResource();
        setupGetWithRestResultExpectations(url, FileEntryResource.class, expectedFileEntry, OK);

        FileEntryResource retrievedFileEntry = service.getFileEntryDetails(projectId, documentConfigId).getSuccess().get();
        Assert.assertEquals(expectedFileEntry, retrievedFileEntry);
    }

    @Test
    public void deleteDocument() {

        String url = String.format("%s/%s/document/config/%s/delete", projectRestURL, projectId, documentConfigId);
        setupDeleteWithRestResultExpectations(url);

        service.deleteDocument(projectId, documentConfigId);
        setupDeleteWithRestResultVerifications(url);
    }

    @Test
    public void submitDocument() {
        String url = String.format("%s/%s/document/config/%s/submit", projectRestURL, projectId, documentConfigId);
        setupPostWithRestResultExpectations(url, null, OK);

        RestResult<Void> result = service.submitDocument(projectId, documentConfigId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void documentDecision() {
        String url = String.format("%s/%s/document/config/%s/decision", projectRestURL, projectId, documentConfigId);
        ProjectDocumentDecision decision = new ProjectDocumentDecision(true, null);
        setupPostWithRestResultExpectations(url, decision, OK);

        RestResult<Void> result = service.documentDecision(projectId, documentConfigId, decision);

        setupPostWithRestResultVerifications(url, Void.class, decision);

        assertTrue(result.isSuccess());
    }
}
