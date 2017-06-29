package org.innovateuk.ifs.thread.attachment.controller;


import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.threads.attachments.controller.ProjectFinanceAttachmentsController;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceAttachmentControllerTest extends BaseControllerMockMVCTest<ProjectFinanceAttachmentsController> {

    @Override
    protected ProjectFinanceAttachmentsController supplyControllerUnderTest() {
        return new ProjectFinanceAttachmentsController(projectFinanceAttachmentServiceMock);
    }

    @Override
    public void setupMockMvc() {
        controller = new ProjectFinanceAttachmentsController(projectFinanceAttachmentServiceMock);
        super.setupMockMvc();
    }

    @Test
    public void testFindOne() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.findOne(id)).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(get("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(attachmentResource)));

        verify(projectFinanceAttachmentServiceMock).findOne(22L);
    }

    @Test
    public void testDownload() throws Exception {
        final Long id = 22L;

        Function<ProjectFinanceAttachmentService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.attachmentFileAndContents(id);

        assertGetFileContents("/project/finance/attachments/download/{attachmentId}", new Object[]{id},
                emptyMap(), projectFinanceAttachmentServiceMock, serviceCallToDownload);
    }

    @Test
    public void testDelete() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.delete(id)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isNoContent());

        verify(projectFinanceAttachmentServiceMock).delete(id);
    }

    @Test
    public void testUpload() throws Exception {
        final Long id = 22L;
        final Long projectId = 77L;
        final AttachmentResource attachmentResource = new AttachmentResource(id, "randomFile.pdf", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(projectId), any(HttpServletRequest.class))).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(post("/project/finance/attachments/{projectId}/upload", projectId)
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(content().json(toJson(attachmentResource)))
                .andExpect(status().isCreated());

        verify(projectFinanceAttachmentServiceMock).upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(projectId), any(HttpServletRequest.class));
    }


    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}

