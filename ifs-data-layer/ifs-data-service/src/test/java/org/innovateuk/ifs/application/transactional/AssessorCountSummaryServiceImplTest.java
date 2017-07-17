package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.junit.Test;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.NOT_ACCEPTED_OR_SUBMITTED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.REJECTED_AND_SUBMITTED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.SUBMITTED_ASSESSMENT_STATES;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
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

    @Test
    public void getAssessorCountSummariesByCompetitionId() {
        final long competitionId = 1L;
        final int pageNumber = 0;
        final int pageSize = 20;

        List<AssessorCountSummaryResource> assessorCountSummaryResources = newAssessorCountSummaryResource().build(2);

        Page<AssessorCountSummaryResource> page = mock(Page.class);

        when(page.getContent()).thenReturn(assessorCountSummaryResources);
        when(page.getTotalElements()).thenReturn(2L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(pageNumber);
        when(page.getSize()).thenReturn(pageSize);

        when(applicationStatisticsRepositoryMock.getAssessorCountSummaryByCompetition(
                eq(competitionId), eq(null), argThat(new PageableMatcher(pageNumber, pageSize)))
        ).thenReturn(page);

        final AssessorCountSummaryPageResource expectedPageResource =
                new AssessorCountSummaryPageResource(2, 1, assessorCountSummaryResources, pageNumber, pageSize);

        AssessorCountSummaryPageResource result =
                service.getAssessorCountSummariesByCompetitionId(competitionId, pageNumber, pageSize)
                        .getSuccessObjectOrThrowException();

        assertEquals(expectedPageResource, result);
    }
}