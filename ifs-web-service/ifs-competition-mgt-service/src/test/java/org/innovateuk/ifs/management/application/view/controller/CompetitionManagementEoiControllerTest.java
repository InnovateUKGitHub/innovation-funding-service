package org.innovateuk.ifs.management.application.view.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionManagementEoiControllerTest extends BaseControllerMockMVCTest<CompetitionManagementEoiController> {

    @Mock
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Override
    protected CompetitionManagementEoiController supplyControllerUnderTest() {
        return new CompetitionManagementEoiController();
    }

    @Test
    public void downloadEOIEvidenceFile() throws Exception {
        long applicationId = 1L;
        ByteArrayResource content = new ByteArrayResource("My content!".getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource()
                .withName("Filename")
                .build();

        when(applicationEoiEvidenceResponseRestService.getEvidenceByApplication(applicationId)).thenReturn(restSuccess(content));
        when(applicationEoiEvidenceResponseRestService.getEvidenceDetailsByApplication(applicationId)).thenReturn(restSuccess(fileEntryResource));

        mockMvc.perform(get("/application/{applicationId}/view-eoi-evidence", applicationId))
                .andExpect(status().isOk());

        verify(applicationEoiEvidenceResponseRestService).getEvidenceByApplication(applicationId);
        verify(applicationEoiEvidenceResponseRestService).getEvidenceDetailsByApplication(applicationId);
    }

}
