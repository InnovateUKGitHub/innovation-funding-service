package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus.FUNDED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationSummaryController> {

    private static final int PAGE_SIZE = 20;

    @Mock
    private ApplicationSummaryService applicationSummaryService;

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Test
    public void searchByCompetitionId() throws Exception {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        long competitionId = 3L;
        int page = 6;

        when(applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}", competitionId)
                .param("page", Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty());
    }

    @Test
    public void searchByCompetitionIdWithSortField() throws Exception {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        long competitionId = 6;
        int page = 6;
        String sort = "id";

        when(applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}", competitionId)
                .param("page", Integer.toString(page))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty());
    }

    @Test
    public void searchByCompetitionIdWithFilter() throws Exception {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        long competitionId = 3L;
        int page = 6;
        String filter = "filter";

        when(applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(filter))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}", competitionId)
                .param("page",Integer.toString(page))
                .param("filter",filter))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(filter));
    }

    @Test
    public void searchSubmittedByCompetitionId() throws Exception {
        long competitionId = 3L;
        int page = 6;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/submitted",competitionId)
                .param("page",Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty(), empty());
    }

    @Test
    public void searchSubmittedByCompetitionIdWithSortField() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String sort = "id";

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/submitted",competitionId)
                .param("page",Integer.toString(page))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty(), empty());
    }

    @Test
    public void searchSubmittedByCompetitionIdWithFilter() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String strFilter = "filter";
        FundingDecisionStatus fundingFilter = FUNDED;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(fundingFilter), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/submitted",competitionId)
                .param("page",Integer.toString(page))
                .param("filter", strFilter)
                .param("fundingFilter", fundingFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(fundingFilter), empty());
    }

    @Test
    public void searchNotSubmittedByCompetitionId() throws Exception {
        long competitionId = 3L;
        int page = 6;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/not-submitted",competitionId)
                .param("page",Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE);
    }

    @Test
    public void searchNotSubmittedByCompetitionIdWithSortField() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String sort = "id";

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/not-submitted",competitionId)
                .param("page",Integer.toString(page))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE);
    }

    @Test
    public void searchWithFundingDecisionByCompetitionId() throws Exception {
        long competitionId = 3L;
        int page = 6;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/with-funding-decision",competitionId)
                .param("page",Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty(), empty());
    }

    @Test
    public void searchWithFundingDecisionByCompetitionIdWithSortField() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String sort = "id";

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/with-funding-decision",competitionId)
                .param("page",Integer.toString(page))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty(), empty());
    }

    @Test
    public void searchWithFundingDecisionByCompetitionIdWithFiltering() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String strFilter = "filter";
        FundingDecisionStatus fundingFilter = FUNDED;
        boolean sendFilter = true;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(sendFilter), of(fundingFilter))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/with-funding-decision",competitionId)
                .param("page",Integer.toString(page))
                .param("filter", strFilter)
                .param("sendFilter", Boolean.toString(sendFilter))
                .param("fundingFilter", fundingFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(sendFilter), of(fundingFilter));
    }

    @Test
    public void searchAllWithChangeAbleFundingDecisionByCompetitionIdWithFiltering() throws Exception {
        long competitionId = 3L;
        String strFilter = "filter";
        FundingDecisionStatus fundingFilter = FUNDED;
        boolean sendFilter = true;

        List<Long> applicationIds = asList(1L, 2L);

        when(applicationSummaryService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(competitionId, of(strFilter), of(sendFilter), of(fundingFilter))).thenReturn(serviceSuccess(applicationIds));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/with-funding-decision",competitionId)
                .param("all", "")
                .param("filter", strFilter)
                .param("sendFilter", Boolean.toString(sendFilter))
                .param("fundingFilter", fundingFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(applicationIds)));

        verify(applicationSummaryService).getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(competitionId, of(strFilter), of(sendFilter), of(fundingFilter));
    }

    @Test
    public void searchAllSubmittedApplicationIdsByCompetitionId() throws Exception {
        long competitionId = 3L;
        String strFilter = "filter";
        FundingDecisionStatus fundingFilter = FUNDED;

        List<Long> applicationIds = asList(1L, 2L);

        when(applicationSummaryService.getAllSubmittedApplicationIdsByCompetitionId(competitionId, of(strFilter), of(fundingFilter))).thenReturn(serviceSuccess(applicationIds));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/all-submitted",competitionId)
                .param("all", "")
                .param("filter", strFilter)
                .param("fundingFilter", fundingFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(applicationIds)));

        verify(applicationSummaryService).getAllSubmittedApplicationIdsByCompetitionId(competitionId, of(strFilter), of(fundingFilter));
    }

    @Test
    public void searchIneligibleByCompetitionId() throws Exception {
        long competitionId = 3L;
        int page = 6;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getIneligibleApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/ineligible",competitionId)
                .param("page",Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getIneligibleApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, empty(), empty());
    }

    @Test
    public void searchIneligibleByCompetitionIdWithSortField() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String sort = "id";

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getIneligibleApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/ineligible",competitionId)
                .param("page",Integer.toString(page))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getIneligibleApplicationSummariesByCompetitionId(competitionId, sort, page, PAGE_SIZE, empty(), empty());
    }

    @Test
    public void searchIneligibleByCompetitionIdWithFilter() throws Exception {
        long competitionId = 3L;
        int page = 6;
        String strFilter = "filter";
        Boolean informFilter = false;

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

        when(applicationSummaryService.getIneligibleApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(informFilter))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-summary/find-by-competition/{compId}/ineligible", competitionId)
                .param("page",Integer.toString(page))
                .param("filter", strFilter)
                .param("informFilter", informFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

        verify(applicationSummaryService).getIneligibleApplicationSummariesByCompetitionId(competitionId, null, page, PAGE_SIZE, of(strFilter), of(informFilter));
    }
}
