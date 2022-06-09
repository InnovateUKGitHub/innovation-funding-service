package org.innovateuk.ifs.thread.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.attachments.controller.ProjectFinanceAttachmentsController;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceAttachmentsControllerDocumentation extends BaseFileControllerMockMVCTest<ProjectFinanceAttachmentsController> {

    private static final String identifier = "project/finance/attachments/{method-name}";

    @Mock
    private ProjectFinanceAttachmentService projectFinanceAttachmentServiceMock;

    @Test
    public void findOne() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234, now());
        when(projectFinanceAttachmentServiceMock.findOne(id)).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(get("/project/finance/attachments/{attachmentId}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(attachmentResource)));

        verify(projectFinanceAttachmentServiceMock).findOne(id);
    }

    @Test
    public void download() throws Exception {
        final Long id = 22L;

        Function<ProjectFinanceAttachmentService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.attachmentFileAndContents(id);

        assertGetFileContents("/project/finance/attachments/download/{attachmentId}", new Object[]{id},
                emptyMap(), projectFinanceAttachmentServiceMock, serviceCallToDownload);
    }

    @Test
    public void delete() throws Exception {
        final Long id = 22L;
        when(projectFinanceAttachmentServiceMock.delete(id)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.delete("/project/finance/attachments/{attachmentId}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent());

        verify(projectFinanceAttachmentServiceMock).delete(id);
    }

    @Test
    public void upload() throws Exception {
        final Long id = 22L;
        final Long projectId = 77L;
        final AttachmentResource attachmentResource = new AttachmentResource(id, "randomFile.pdf", "application/pdf", 1234, now());
        when(projectFinanceAttachmentServiceMock.upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(projectId), any(HttpServletRequest.class))).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(post("/project/finance/attachments/{projectId}/upload", projectId)
                .param("filename", attachmentResource.name)
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(content().json(toJson(attachmentResource)))
                .andExpect(status().isCreated());

        verify(projectFinanceAttachmentServiceMock).upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(projectId), any(HttpServletRequest.class));
    }

    @Override
    public void setupMockMvc() {
        controller = new ProjectFinanceAttachmentsController(projectFinanceAttachmentServiceMock);
        super.setupMockMvc();
    }

    @Override
    protected ProjectFinanceAttachmentsController supplyControllerUnderTest() {
        return null;
    }

    private HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}