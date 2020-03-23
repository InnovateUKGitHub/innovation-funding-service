package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.PageableMatcher.srt;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * Tests for {@link ApplicationCountSummaryServiceImpl}
 */
public class ApplicationCountSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationCountSummaryService> {

    private long competitionId = 1L;
    private Role leadApplicationRole;
    private Role applicantRole;
    private List<ApplicationStatistics> applicationStatistics;
    private Page<ApplicationStatistics> page;
    private ApplicationCountSummaryPageResource resource;

    @Mock
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Mock
    private ApplicationCountSummaryPageMapper applicationCountSummaryPageMapper;



    @Override
    protected ApplicationCountSummaryService supplyServiceUnderTest() {
        return new ApplicationCountSummaryServiceImpl();
    }

    @Before
    public void setup() {
        applicationStatistics = newApplicationStatistics()
                .withProcessRoles(
                        newProcessRole()
                                .withRole(applicantRole, leadApplicationRole)
                                .withUser(UserBuilder.newUser().build())
                                .build(2),
                        newProcessRole()
                                .withRole(leadApplicationRole, applicantRole)
                                .withUser(UserBuilder.newUser().build())
                                .build(2)
                )
                .build(2);

        page = mock(Page.class);
        when(page.getContent()).thenReturn(applicationStatistics);

        resource = mock(ApplicationCountSummaryPageResource.class);
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        when(applicationStatisticsRepository.findByCompetitionAndApplicationProcessActivityStateIn(eq(competitionId), eq(SUBMITTED_STATES), eq("filter"), argThat(new PageableMatcher(0, 20)))).thenReturn(page);
        when(applicationCountSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationCountSummaryPageResource> result = service.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, ofNullable("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndAssessorId() {
        Page<ApplicationCountSummaryResource> pageCount = mock(Page.class);
        when(applicationStatisticsRepository.findStatisticsForApplicationsNotAssignedTo(eq(competitionId), eq(1L), eq("asd"), argThat(new PageableMatcher(0, 20, srt("id", ASC ))))).thenReturn(pageCount);

        ServiceResult<ApplicationCountSummaryPageResource> result = service.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, 1L, 0, 20, Sort.APPLICATION_NUMBER, "asd");

        assertTrue(result.isSuccess());
    }

    @Test
    public void getApplicationIdsByCompetitionIdAndAssessorId() {
        List<Long> list = asList(1L, 2L);
        when(applicationStatisticsRepository.findApplicationIdsNotAssignedTo(eq(competitionId), eq(1L), eq("asd"))).thenReturn(list);

        ServiceResult<List<Long>> result = service.getApplicationIdsByCompetitionIdAndAssessorId(competitionId, 1L, "asd");

        assertTrue(result.isSuccess());
    }
}
