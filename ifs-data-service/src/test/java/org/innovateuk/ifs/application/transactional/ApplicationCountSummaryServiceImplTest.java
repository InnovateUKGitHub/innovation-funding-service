package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ApplicationCountSummaryServiceImpl}
 */
public class ApplicationCountSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationCountSummaryService> {

    @Override
    protected ApplicationCountSummaryService supplyServiceUnderTest() {
        return new ApplicationCountSummaryServiceImpl();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        Long competitionId = 1L;

        List<ApplicationStatistics> applicationStatistics = newApplicationStatistics().build(2);
        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource().build(2);
        when(applicationStatisticsRepositoryMock.findByCompetition(competitionId)).thenReturn(applicationStatistics);
        when(applicationCountSummaryMapperMock.mapToResource(applicationStatistics.get(0))).thenReturn(summaryResources.get(0));
        when(applicationCountSummaryMapperMock.mapToResource(applicationStatistics.get(1))).thenReturn(summaryResources.get(1));


        List<ApplicationCountSummaryResource> result = service.getApplicationCountSummariesByCompetitionId(competitionId).getSuccessObject();
        verify(applicationStatisticsRepositoryMock, only()).findByCompetition(competitionId);
        verify(applicationCountSummaryMapperMock, times(2)).mapToResource(isA(ApplicationStatistics.class));
        assertEquals(summaryResources, result);
    }
}
