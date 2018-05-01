package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.interview.transactional.InterviewResponseService;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewResponseControllerTest extends BaseControllerMockMVCTest<InterviewResponseController> {

    @Override
    protected InterviewResponseController supplyControllerUnderTest() {
        return new InterviewResponseController(null);
    }

    @Test
    public void testUploadResponse() throws Exception {
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

    @Test
    public void testDeleteResponse() throws Exception {
        final long applicationId = 22L;
        when(interviewResponseService.deleteResponse(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-response/{applicationId}", applicationId))
                .andExpect(status().isNoContent());

        verify(interviewResponseService).deleteResponse(applicationId);
    }

    @Test
    public void testDownloadResponse() throws Exception {
        final long applicationId = 22L;

        Function<InterviewResponseService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadResponse(applicationId);

        assertGetFileContents("/interview-response/{applicationId}", new Object[]{applicationId},
                emptyMap(), interviewResponseService, serviceCallToDownload);
    }

    @Test
    public void testFindResponse() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(interviewResponseService.findResponse(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/interview-response/details/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(interviewResponseService).findResponse(applicationId);
    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}