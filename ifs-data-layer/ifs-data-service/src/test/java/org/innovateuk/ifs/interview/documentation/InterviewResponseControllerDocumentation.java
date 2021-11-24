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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(get("/interview-response/details/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(interviewResponseService).findResponse(applicationId);
    }

    @Test
    public void downloadResponse() throws Exception {
        final long applicationId = 22L;

        Function<InterviewResponseService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadResponse(applicationId);

        assertGetFileContents("/interview-response/{applicationId}", new Object[]{applicationId},
                emptyMap(), interviewResponseService, serviceCallToDownload);
    }

    @Test
    public void deleteResponse() throws Exception {
        final long applicationId = 22L;
        when(interviewResponseService.deleteResponse(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.delete("/interview-response/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent());

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
                .andExpect(status().isCreated());

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
