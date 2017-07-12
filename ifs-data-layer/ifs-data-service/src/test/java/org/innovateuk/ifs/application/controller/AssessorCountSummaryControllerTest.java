package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorCountSummaryControllerTest extends BaseControllerMockMVCTest<AssessorCountSummaryController> {

    @Override
    protected AssessorCountSummaryController supplyControllerUnderTest() {
        return new AssessorCountSummaryController();
    }

    @Test
    public void getAssessorCountSummariesByCompetitionId() throws Exception {
        final long competitionId = 1L;
        final int pageNumber = 0;
        final int pageSize = 20;

        AssessorCountSummaryPageResource pageResource = newAssessorCountSummaryPageResource().build();

        when(assessorCountSummaryServiceMock.getAssessorCountSummariesByCompetitionId(competitionId, pageNumber, pageSize)).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/assessorCountSummary/findByCompetitionId/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(assessorCountSummaryServiceMock, only()).getAssessorCountSummariesByCompetitionId(competitionId, pageNumber, pageSize);
    }
}
