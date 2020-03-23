package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.transactional.ApplicationCountSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationCountSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationCountSummaryController> {

    @Mock
    private ApplicationCountSummaryService applicationCountSummaryServiceMock;

    @Override
    protected ApplicationCountSummaryController supplyControllerUnderTest() {
        return new ApplicationCountSummaryController();
    }

    @Test
    public void applicationCountSummariesByCompetitionId() throws Exception {
        Long competitionId = 1L;

        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty())).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/application-count-summary/find-by-competition-id/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(applicationCountSummaryServiceMock, only()).getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty());
    }

    @Test
    public void applicationCountSummariesByCompetitionIdFiltered() throws Exception {
        Long competitionId = 1L;
        int page = 2;
        int pageSize = 3;
        String filter = "filter";

        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId, page, pageSize, ofNullable(filter))).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/application-count-summary/find-by-competition-id/{competitionId}?page={page}&size={pageSize}&filter={filter}", competitionId, page, pageSize, filter))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(applicationCountSummaryServiceMock, only()).getApplicationCountSummariesByCompetitionId(competitionId, page, pageSize, ofNullable(filter));
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndAssessorId() throws Exception {
        long competitionId = 1L;
        long assessorId = 10L;
        int page = 2;
        int pageSize = 3;
        String filter = "ads";
        Sort sortField = Sort.ACCEPTED;

        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId, page, pageSize, sortField, filter)).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(get("/application-count-summary/find-by-competition-id-and-assessor-id/{competitionId}/{assessorId}?page={page}&size={pageSize}&sort={sortField}&filter={filter}", competitionId, assessorId, page, pageSize, sortField, filter))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResource)));

        verify(applicationCountSummaryServiceMock, only()).getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId, page, pageSize, sortField, filter);

    }
}
