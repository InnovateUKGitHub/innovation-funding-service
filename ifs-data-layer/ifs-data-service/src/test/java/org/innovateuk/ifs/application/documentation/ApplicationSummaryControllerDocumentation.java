package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.application.builder.PreviousApplicationResourceBuilder.newPreviousApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationSummaryDocs.APPLICATION_SUMMARY_RESOURCE_BUILDER;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

public class ApplicationSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationSummaryController> {
    @Mock
    private ApplicationSummaryService applicationSummaryService;

    private static String baseUrl = "/application-summary";

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Test
    public void getWithFundingDecisionApplicationSummariesByCompetitionId() throws Exception {
        final Long competitionId = 1L;
        final String sort = "id";
        final Integer pageIndex = 0;
        final Integer size = 20;
        final Integer totalPages = pageIndex + 2; // In this test we are giving a full page of results before the last page.
        String filter = "filter";
        Boolean sendFilter = true;
        FundingDecisionStatus fundingFilter = FundingDecisionStatus.UNDECIDED;

        List<ApplicationSummaryResource> applications = APPLICATION_SUMMARY_RESOURCE_BUILDER.build(5);

        ApplicationSummaryPageResource pageResource = new ApplicationSummaryPageResource(totalPages * size, totalPages, applications, pageIndex, size);

        when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, sort, pageIndex, size, Optional.of(filter), Optional.of(sendFilter), Optional.of(fundingFilter))).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(
                get(baseUrl + "/find-by-competition/{competitionId}/with-funding-decision", competitionId)
                        .param("page", pageIndex + "")
                        .param("sort", sort)
                        .param("size", size + "")
                        .param("filter", filter)
                        .param("sendFilter", sendFilter.toString())
                        .param("fundingFilter", fundingFilter.toString())
                        .contentType(APPLICATION_JSON)
                        .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId() throws Exception {
        final Long competitionId = 1L;
        String filter = "filter";
        Boolean sendFilter = false;
        FundingDecisionStatus fundingFilter = FundingDecisionStatus.FUNDED;
        List<Long> applicationIds = asList(1L, 2L, 3L);

        when(applicationSummaryService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(competitionId, Optional.of(filter), Optional.of(sendFilter), Optional.of(fundingFilter))).thenReturn(serviceSuccess(applicationIds));

        mockMvc.perform(
                get(baseUrl + "/find-by-competition/{competitionId}/with-funding-decision", competitionId)
                        .param("all", "")
                        .param("filter", filter)
                        .param("sendFilter", sendFilter.toString())
                        .param("fundingFilter", fundingFilter.toString())
                        .contentType(APPLICATION_JSON)
                        .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getAllSubmittedApplicationIdsByCompetitionId() throws Exception {
        final Long competitionId = 1L;
        String filter = "filter";
        FundingDecisionStatus fundingFilter = FundingDecisionStatus.FUNDED;
        List<Long> applicationIds = asList(1L, 2L, 3L);

        when(applicationSummaryService.getAllSubmittedApplicationIdsByCompetitionId(competitionId, Optional.of(filter), Optional.of(fundingFilter))).thenReturn(serviceSuccess(applicationIds));

        mockMvc.perform(
                get(baseUrl + "/find-by-competition/{competitionId}/all-submitted", competitionId)
                        .param("all", "")
                        .param("filter", filter)
                        .param("fundingFilter", fundingFilter.toString())
                        .contentType(APPLICATION_JSON)
                        .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getIneligibleApplicationsSummariesByCompetitionId() throws Exception {
        long competitionId = 1L;
        String sort = "id";
        int pageIndex = 0;
        int size = 20;
        String filter = "filter";
        Boolean informFilter = true;
        int totalPages = pageIndex + 2;

        List<ApplicationSummaryResource> applications = APPLICATION_SUMMARY_RESOURCE_BUILDER.build(5);
        ApplicationSummaryPageResource pageResource = new ApplicationSummaryPageResource(totalPages * size, totalPages, applications, pageIndex, size);
        when(applicationSummaryService.getIneligibleApplicationSummariesByCompetitionId(competitionId, sort, pageIndex, size, Optional.of(filter), Optional.of(informFilter))).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(
                get(baseUrl + "/find-by-competition/{competitionId}/ineligible", competitionId)
                        .param("page", pageIndex + "")
                        .param("sort", sort)
                        .param("size", size + "")
                        .param("filter", filter)
                        .param("informFilter", informFilter.toString())
                        .contentType(APPLICATION_JSON)
                        .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getPreviousApplications() throws Exception {
        long competitionId = 4L;

        when(applicationSummaryService.getPreviousApplications(competitionId)).thenReturn(serviceSuccess(newPreviousApplicationResource().build(1)));

        mockMvc.perform(
                get(baseUrl + "/find-by-competition/{competitionId}/previous", competitionId)
                        .contentType(APPLICATION_JSON));

        verify(applicationSummaryService).getPreviousApplications(competitionId);
    }
}
