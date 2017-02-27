package org.innovateuk.ifs.thread.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.FileUploadHeadersSet;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.threads.attachments.controller.ProjectFinanceAttachmentsController;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceAttachmentsControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceAttachmentsController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup() {
        this.document = document("project/finance/attachments/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void testFindOne() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.findOne(id)).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(get("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(attachmentResource)))
                .andDo(this.document.document(
                        pathParameters(parameterWithName("attachmentId").description("Id of the Attachment to be fetched")),
                        responseFields(attachmentFields())));

        verify(projectFinanceAttachmentServiceMock).findOne(id);
    }

    @Test
    public void testDownload() throws Exception {
        final Long id = 22L;

        Function<ProjectFinanceAttachmentService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.attachmentFileAndContents(id);

        assertGetFileContents("/project/finance/attachments/download/{attachmentId}", new Object[]{id},
                emptyMap(), projectFinanceAttachmentServiceMock, serviceCallToDownload)
                .andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void testDelete() throws Exception {
        final Long id = 22L;
        AttachmentResource attachmentResource = new AttachmentResource(id, "name", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.delete(id)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/finance/attachments/{attachmentId}", id))
                .andExpect(status().isNoContent())
                .andDo(this.document.document(
                        pathParameters(parameterWithName("attachmentId").description("Id of the Attachment to be deleted")))
                );

        verify(projectFinanceAttachmentServiceMock).delete(id);
    }

    @Test
    public void testUpload() throws Exception {
        final Long id = 22L;
        final AttachmentResource attachmentResource = new AttachmentResource(id, "randomFile.pdf", "application/pdf", 1234);
        when(projectFinanceAttachmentServiceMock.upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                any(HttpServletRequest.class))).thenReturn(serviceSuccess(attachmentResource));

        mockMvc.perform(post("/project/finance/attachments/upload")
                .param("filename", attachmentResource.name)
                .headers(new FileUploadHeadersSet("application/pdf", 1234).unwrap()))
                .andExpect(content().json(toJson(attachmentResource)))
                .andExpect(status().isCreated())
                .andDo(document.document(
                        requestParameters(parameterWithName("filename").description("The filename of the file being uploaded")),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"),
                                headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes")
                        ),
                        responseFields(attachmentFields())
                ));

        verify(projectFinanceAttachmentServiceMock).upload(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                any(HttpServletRequest.class));
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

}