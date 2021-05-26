package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.transactional.AssessorCountSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorCountSummaryControllerTest extends BaseControllerMockMVCTest<AssessorCountSummaryController> {

    @Mock
    private AssessorCountSummaryService assessorCountSummaryServiceMock;

    @Override
    protected AssessorCountSummaryController supplyControllerUnderTest() {
        return new AssessorCountSummaryController();
    }

    @Test
    public void getAssessorCountSummariesByCompetitionId() throws Exception {
        final long competitionId = 1L;
        final long assessmentPeriodId = 2L;
        final int pageNumber = 0;
        final int pageSize = 20;
        final String assessorFilter = "";

        AssessorCountSummaryPageResource pageResource = newAssessorCountSummaryPageResource().build();

        when(assessorCountSummaryServiceMock.getAssessorCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionId, assessmentPeriodId, assessorFilter, pageNumber, pageSize)).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/assessor-count-summary/find-by-competition-id-and-assessment-period-id/{competitionId}/{assessmentPeriodId}", competitionId, assessmentPeriodId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(assessorCountSummaryServiceMock, only()).getAssessorCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionId, assessmentPeriodId, assessorFilter, pageNumber, pageSize);
    }
}