package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.interview.controller.InterviewResponseController;
import org.innovateuk.ifs.interview.transactional.InterviewResponseService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.FileEntryDocs.fileEntryResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewResponseControllerDocumentation extends BaseFileControllerMockMVCTest<InterviewResponseController> {

    @Mock
    private InterviewResponseService interviewResponseService;

    @Override
    public InterviewResponseController supplyControllerUnderTest() {
        return new InterviewResponseController(null);
    }

    @Test
    public void findResponse() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(interviewResponseService.findResponse(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/interview-response/details/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)))
                .andDo(document("interview-response/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("Id of the Attachment to be fetched")),
                        responseFields(fileEntryResourceFields)));

        verify(interviewResponseService).findResponse(applicationId);
    }

    @Test
    public void downloadResponse() throws Exception {
        final long applicationId = 22L;

        Function<InterviewResponseService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadResponse(applicationId);

        assertGetFileContents("/interview-response/{applicationId}", new Object[]{applicationId},
                emptyMap(), interviewResponseService, serviceCallToDownload)
                .andDo(documentFileGetContentsMethod("interview-response/{method-name}"));
    }

    @Test
    public void deleteResponse() throws Exception {
        final long applicationId = 22L;
        when(interviewResponseService.deleteResponse(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/interview-response/{applicationId}", applicationId))
                .andExpect(status().isNoContent())
                .andDo(document("interview-response/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("Id of the application to have attachment deleted")))
                );

        verify(interviewResponseService).deleteResponse(applicationId);
    }

    @Test
    public void uploadResponse() throws Exception {
        final long applicationId = 77L;
        when(interviewResponseService.uploadResponse(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-response/{applicationId}", applicationId)
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated())
                .andDo(document("interview-response/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("The application in which the feedback will be attached.")),
                        requestParameters(parameterWithName("filename").description("The filename of the file being uploaded")),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf")
                        )
                ));

        verify(interviewResponseService).uploadResponse(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class));
    }

    private HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}
