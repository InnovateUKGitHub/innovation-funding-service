package org.innovateuk.ifs.thread.attachment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.finance.controller.ProjectFinanceQueriesController;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.innovateuk.ifs.threads.attachments.controller.ProjectFinanceAttachmentsController;
import org.innovateuk.ifs.threads.attachments.repository.AttachmentRepository;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
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
    public void testUpload() throws Exception {
        final Long id = 22L;
        final AttachmentResource attachmentResource = new AttachmentResource(id, "randomFile.pdf", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                any(HttpServletRequest.class))).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(post("/project/finance/attachments/upload")
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(content().json(toJson(attachmentResource)))
                .andExpect(status().isCreated());

        verify(projectFinanceAttachmentServiceMock).upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                any(HttpServletRequest.class));
    }

//    @Test
//    public void testAddPost() throws Exception {
//        Long threadId = 22L;
//        PostResource post = new PostResource(33L, null, null, null, null);
//        when(projectFinanceAttachmentServiceMock.addPost(post, threadId)).thenReturn(serviceSuccess());
//
//        mockMvc.perform(post("/project/finance/queries/{threadId}/post", threadId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(post)))
//                .andExpect(status().isCreated());
//
//        verify(projectFinanceAttachmentServiceMock).addPost(post, threadId);
//    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}

