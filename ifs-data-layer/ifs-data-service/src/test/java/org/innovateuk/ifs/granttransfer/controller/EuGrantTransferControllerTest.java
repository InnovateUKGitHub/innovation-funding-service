package org.innovateuk.ifs.granttransfer.controller;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.transactional.EuGrantTransferService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantTransferControllerTest extends BaseFileControllerMockMVCTest<EuGrantTransferController> {

    private static final long COMPETITION_ID = 1L;

    @Mock
    private EuGrantTransferService euGrantTransferService;

    @Override
    protected EuGrantTransferController supplyControllerUnderTest() {
        return new EuGrantTransferController();
    }

    @Test
    public void uploadGrantAgreement() throws Exception {
        final long applicationId = 77L;
        when(euGrantTransferService.uploadGrantAgreement(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-grant-transfer/grant-agreement/{applicationId}", applicationId)
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated());

        verify(euGrantTransferService).uploadGrantAgreement(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class));
    }

    @Test
    public void deleteGrantAgreement() throws Exception {
        final long applicationId = 22L;
        when(euGrantTransferService.deleteGrantAgreement(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/eu-grant-transfer/grant-agreement/{applicationId}", applicationId))
                .andExpect(status().isNoContent());

        verify(euGrantTransferService).deleteGrantAgreement(applicationId);
    }

    @Test
    public void downloadGrantAgreement() throws Exception {
        final long applicationId = 22L;

        Function<EuGrantTransferService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadGrantAgreement(applicationId);

        assertGetFileContents("/eu-grant-transfer/grant-agreement/{applicationId}", new Object[]{applicationId},
                emptyMap(), euGrantTransferService, serviceCallToDownload);
    }

    @Test
    public void findGrantAgreement() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(euGrantTransferService.findGrantAgreement(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/eu-grant-transfer/grant-agreement-details/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(euGrantTransferService).findGrantAgreement(applicationId);
    }

    @Test
    public void getGrantTransferByApplicationId() throws Exception {
        final long applicationId = 22L;
        EuGrantTransferResource grantTransferResource = newEuGrantTransferResource().build();
        when(euGrantTransferService.getGrantTransferByApplicationId(applicationId)).thenReturn(serviceSuccess(grantTransferResource));

        mockMvc.perform(get("/eu-grant-transfer/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(grantTransferResource)));

        verify(euGrantTransferService).getGrantTransferByApplicationId(applicationId);
    }

    @Test
    public void updateGrantTransferByApplicationId() throws Exception {
        final long applicationId = 22L;
        EuGrantTransferResource grantTransferResource = newEuGrantTransferResource().build();
        when(euGrantTransferService.updateGrantTransferByApplicationId(grantTransferResource, applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-grant-transfer/{applicationId}", applicationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grantTransferResource)))
                .andExpect(status().isOk());

        verify(euGrantTransferService).updateGrantTransferByApplicationId(grantTransferResource, applicationId);
    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}