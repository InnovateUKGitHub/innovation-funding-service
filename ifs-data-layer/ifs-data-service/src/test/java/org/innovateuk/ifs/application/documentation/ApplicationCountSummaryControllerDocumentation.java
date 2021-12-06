package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationCountSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.transactional.ApplicationCountSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationCountDocs.applicationCountSummaryResourceBuilder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationCountSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationCountSummaryController> {

    @Mock
    private ApplicationCountSummaryService applicationCountSummaryServiceMock;

    @Override
    protected ApplicationCountSummaryController supplyControllerUnderTest() {
        return new ApplicationCountSummaryController();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() throws Exception {
        Long competitionId = 1L;
        ApplicationCountSummaryResource applicationCountSummaryResource = applicationCountSummaryResourceBuilder.build();
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();
        pageResource.setContent(singletonList(applicationCountSummaryResource));

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty())).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/application-count-summary/find-by-competition-id/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(applicationCountSummaryServiceMock).getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty());
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndAssessorId() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentPeriodId = 1L;
        Sort sortField = Sort.APPLICATION_NUMBER;
        String filter = "";
        ApplicationCountSummaryResource applicationCountSummaryResource = applicationCountSummaryResourceBuilder.build();
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();
        pageResource.setContent(singletonList(applicationCountSummaryResource));

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId, assessmentPeriodId, 0, 20, sortField, "")).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/application-count-summary/find-by-competition-id-and-assessor-id-and-assessment-period-id/{competitionId}/{assessorId}/{assessmentPeriodId}?sort={sortField}&filter={filter}", competitionId, assessorId, assessmentPeriodId, sortField, filter)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(applicationCountSummaryServiceMock).getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId,assessmentPeriodId, 0, 20, sortField, "");
    }

    @Test
    public void getApplicationIdsByCompetitionIdAndAssessorId() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentPeriodId = 1L;
        String filter = "";

        when(applicationCountSummaryServiceMock.getApplicationIdsByCompetitionIdAndAssessorId(competitionId, assessorId, assessmentPeriodId,"")).thenReturn(serviceSuccess(emptyList()));

        mockMvc.perform(get("/application-count-summary/find-ids-by-competition-id-and-assessor-id-and-assessment-period-id/{competitionId}/{assessorId}/{assessmentPeriodId}?filter={filter}", competitionId, assessorId, assessmentPeriodId, filter)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(applicationCountSummaryServiceMock).getApplicationIdsByCompetitionIdAndAssessorId(competitionId, assessorId, assessmentPeriodId,"");
    }
}
