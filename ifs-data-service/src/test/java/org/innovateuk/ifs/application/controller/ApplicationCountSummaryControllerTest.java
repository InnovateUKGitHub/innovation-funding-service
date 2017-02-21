package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationCountSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationCountSummaryController> {

    @Override
    protected ApplicationCountSummaryController supplyControllerUnderTest() {
        return new ApplicationCountSummaryController();
    }

    @Test
    public void applicationCountSummariesByCompetitionId() throws Exception {
        Long competitionId = 1L;

        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, null)).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/applicationCountSummary/findByCompetitionId/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(applicationCountSummaryServiceMock, only()).getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, null);
    }
}
