package org.innovateuk.ifs.granttransfer.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.granttransfer.controller.EuGrantTransferController;
import org.innovateuk.ifs.granttransfer.transactional.EuGrantTransferService;
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
import static org.innovateuk.ifs.documentation.EuGrantTransferDocs.EU_GRANT_TRANSFER_RESOURCE;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantTransferControllerDocumentation extends BaseFileControllerMockMVCTest<EuGrantTransferController> {

    @Mock
    private EuGrantTransferService euGrantTransferService;

    @Override
    public EuGrantTransferController supplyControllerUnderTest() {
        return new EuGrantTransferController();
    }

    @Test
    public void findGrantAgreement() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(euGrantTransferService.findGrantAgreement(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/eu-grant-transfer/grant-agreement-details/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(euGrantTransferService).findGrantAgreement(applicationId);
    }

    @Test
    public void downloadGrantAgreement() throws Exception {
        final long applicationId = 22L;

        Function<EuGrantTransferService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadGrantAgreement(applicationId);

        assertGetFileContents("/eu-grant-transfer/grant-agreement/{applicationId}", new Object[]{applicationId},
                emptyMap(), euGrantTransferService, serviceCallToDownload)
                .andExpect(status().isOk());
    }

    @Test
    public void deleteGrantAgreement() throws Exception {
        final long applicationId = 22L;
        when(euGrantTransferService.deleteGrantAgreement(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/eu-grant-transfer/grant-agreement/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent());

        verify(euGrantTransferService).deleteGrantAgreement(applicationId);
    }

    @Test
    public void uploadGrantAgreement() throws Exception {
        final long applicationId = 77L;
        when(euGrantTransferService.uploadGrantAgreement(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-grant-transfer/grant-agreement/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated());

        verify(euGrantTransferService).uploadGrantAgreement(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class));
    }

    @Test
    public void getGrantTransferByApplicationId() throws Exception {
        final long applicationId = 22L;
        when(euGrantTransferService.getGrantTransferByApplicationId(applicationId)).thenReturn(serviceSuccess(EU_GRANT_TRANSFER_RESOURCE));

        mockMvc.perform(get("/eu-grant-transfer/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(EU_GRANT_TRANSFER_RESOURCE)));

        verify(euGrantTransferService).getGrantTransferByApplicationId(applicationId);
    }

    @Test
    public void updateGrantTransferByApplicationId() throws Exception {
        final long applicationId = 22L;
        when(euGrantTransferService.updateGrantTransferByApplicationId(EU_GRANT_TRANSFER_RESOURCE, applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-grant-transfer/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EU_GRANT_TRANSFER_RESOURCE)))
                .andExpect(status().isOk());

        verify(euGrantTransferService).updateGrantTransferByApplicationId(EU_GRANT_TRANSFER_RESOURCE, applicationId);
    }

    private HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}
