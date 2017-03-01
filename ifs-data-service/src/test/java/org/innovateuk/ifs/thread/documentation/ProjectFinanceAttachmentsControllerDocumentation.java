package org.innovateuk.ifs.thread.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.threads.attachments.controller.ProjectFinanceAttachmentsController;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AttachmentDocs.attachmentFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceAttachmentsControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceAttachmentsController> {

    private static final String identifier = "project/finance/attachments/{method-name}";


    @Test
    public void findOne() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.findOne(id)).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(get("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(attachmentResource)))
                .andDo(document(identifier,
                        pathParameters(parameterWithName("attachmentId").description("Id of the Attachment to be fetched")),
                        responseFields(attachmentFields())));

        verify(projectFinanceAttachmentServiceMock).findOne(id);
    }

    @Test
    public void download() throws Exception {
        final Long id = 22L;

        Function<ProjectFinanceAttachmentService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.attachmentFileAndContents(id);

        assertGetFileContents("/project/finance/attachments/download/{attachmentId}", new Object[]{id},
                emptyMap(), projectFinanceAttachmentServiceMock, serviceCallToDownload)
                .andDo(documentFileGetContentsMethod(document(identifier, preprocessResponse(prettyPrint()))));
    }

    @Test
    public void delete() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.delete(id)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isNoContent())
                .andDo(document(identifier,
                        pathParameters(parameterWithName("attachmentId").description("Id of the Attachment to be deleted")))
                );

        verify(projectFinanceAttachmentServiceMock).delete(id);
    }

    @Test
    public void upload() throws Exception {
        final Long id = 22L;
        final Long projectId = 77L;
        final AttachmentResource attachmentResource = new AttachmentResource(id, "randomFile.pdf", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(projectId), any(HttpServletRequest.class))).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(post("/project/finance/attachments/{projectId}/upload", projectId)
                .param("filename", attachmentResource.name)
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(content().json(toJson(attachmentResource)))
                .andExpect(status().isCreated())
                .andDo(document(identifier,
                        pathParameters(parameterWithName("projectId").description("The Id of the Project under which this Attachment is being uploaded.")),
                        requestParameters(parameterWithName("filename").description("The filename of the file being uploaded")),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf")
                        ),
                        responseFields(attachmentFields())
                ));

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

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}