package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AssessorCountSummaryServiceImpl}
 */
public class AssessorCountSummaryServiceImplTest extends BaseServiceUnitTest<AssessorCountSummaryService> {

    @Override
    protected AssessorCountSummaryService supplyServiceUnderTest() {
        return new AssessorCountSummaryServiceImpl();
    }

    @Mock
    private ApplicationStatisticsRepository applicationStatisticsRepositoryMock;

    @Test
    public void getAssessorCountSummariesByCompetitionId() {
        final long competitionId = 1L;
        final int pageNumber = 0;
        final int pageSize = 20;
        final Optional<Long> innovationSectorId = Optional.of(5L);
        final String assessorFilter = "";
        final Optional<BusinessType> businessType = Optional.of(BusinessType.ACADEMIC);

        List<AssessorCountSummaryResource> assessorCountSummaryResources = newAssessorCountSummaryResource().build(2);

        Page<AssessorCountSummaryResource> page = mock(Page.class);

        when(page.getContent()).thenReturn(assessorCountSummaryResources);
        when(page.getTotalElements()).thenReturn(2L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(pageNumber);
        when(page.getSize()).thenReturn(pageSize);

        when(applicationStatisticsRepositoryMock.getAssessorCountSummaryByCompetitionAndAssessorNameLike(
                eq(competitionId), eq(assessorFilter), argThat(new PageableMatcher(pageNumber, pageSize, PageableMatcher.srt("user.firstName", Sort.Direction.ASC))))
        ).thenReturn(page);

        final AssessorCountSummaryPageResource expectedPageResource =
                new AssessorCountSummaryPageResource(2, 1, assessorCountSummaryResources, pageNumber, pageSize);

        AssessorCountSummaryPageResource result =
                service.getAssessorCountSummariesByCompetitionId(competitionId, assessorFilter, pageNumber, pageSize)
                        .getSuccess();

        assertEquals(expectedPageResource, result);
    }
}