package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelControllerTest extends BaseControllerMockMVCTest<AssessmentPanelController> {

    long applicationId = 1L;

    @Override
    public AssessmentPanelController supplyControllerUnderTest() {
        return new AssessmentPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(assessmentPanelServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unAssignApplication() throws Exception {
        when(assessmentPanelServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }
}
