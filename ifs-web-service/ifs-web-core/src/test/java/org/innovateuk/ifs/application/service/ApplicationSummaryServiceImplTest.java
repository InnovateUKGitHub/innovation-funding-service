package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryServiceImplTest {

    @InjectMocks
    private ApplicationSummaryServiceImpl service;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Test
    public void testSummaryByCompetitionId() {
        CompetitionSummaryResource resource = new CompetitionSummaryResource();
        when(applicationSummaryRestService.getCompetitionSummary(Long.valueOf(123L))).thenReturn(restSuccess(resource));

        CompetitionSummaryResource result = service.getCompetitionSummaryByCompetitionId(Long.valueOf(123L));

        assertEquals(resource, result);
    }

    @Test
    public void testFindByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryRestService.getAllApplications(Long.valueOf(123L), "sort", 0, 20, "filter")).thenReturn(restSuccess(resource));

        ApplicationSummaryPageResource result = service.findByCompetitionId(Long.valueOf(123L), "sort", 0, 20, "filter");

        assertEquals(resource, result);
    }

    @Test
    public void testFindSubmittedByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryRestService.getSubmittedApplications(Long.valueOf(123L), "sort", 0, 20, "filter")).thenReturn(restSuccess(resource));

        ApplicationSummaryPageResource result = service.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20, "filter");

        assertEquals(resource, result);
    }

    @Test
    public void testFindNotSubmittedByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryRestService.getNonSubmittedApplications(Long.valueOf(123L), "sort", 0, 20, "filter")).thenReturn(restSuccess(resource));

        ApplicationSummaryPageResource result = service.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20, "filter");

        assertEquals(resource, result);
    }

    @Test
    public void testFindRequiringFeedbackByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryRestService.getFeedbackRequiredApplications(Long.valueOf(123L), "sort", 0, 20, "filter")).thenReturn(restSuccess(resource));

        ApplicationSummaryPageResource result = service.getApplicationsRequiringFeedbackByCompetitionId(Long.valueOf(123L), "sort", 0, 20, "filter");

        assertEquals(resource, result);
    }

    @Test
    public void testFindRequiringFeedbackCountByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        resource.setTotalElements(987L);
        when(applicationSummaryRestService.getFeedbackRequiredApplications(Long.valueOf(123L), null, 0, 1, null)).thenReturn(restSuccess(resource));

        Long result = service.getApplicationsRequiringFeedbackCountByCompetitionId(Long.valueOf(123L));

        assertEquals(Long.valueOf(987L), result);
    }

    @Test
    public void testGetApplicationsWithFundingDecisionByCompetitionId() {
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryRestService.getWithFundingDecisionApplications(Long.valueOf(123L), "sort", 0, 20, "", Optional.of(true), Optional.of(UNFUNDED))).thenReturn(restSuccess(resource));

        ApplicationSummaryPageResource result = service.getWithFundingDecisionApplications(Long.valueOf(123L), "sort", 0, 20, "", Optional.of(true), Optional.of(UNFUNDED));

        assertEquals(resource, result);
    }
}
