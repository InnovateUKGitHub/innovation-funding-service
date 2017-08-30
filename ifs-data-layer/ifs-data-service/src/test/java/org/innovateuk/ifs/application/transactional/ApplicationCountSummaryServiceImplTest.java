package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.PageableMatcher.srt;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
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

    @Override
    protected ApplicationCountSummaryService supplyServiceUnderTest() {
        return new ApplicationCountSummaryServiceImpl();
    }

    @Before
    public void setup() {
        leadApplicationRole = newRole()
                .withType(LEADAPPLICANT)
                .build();
        applicantRole = newRole()
                .withType(APPLICANT)
                .build();

        applicationStatistics = newApplicationStatistics()
                .withProcessRoles(
                        newProcessRole()
                                .withRole(applicantRole, leadApplicationRole)
                                .build(2),
                        newProcessRole()
                                .withRole(leadApplicationRole, applicantRole)
                                .build(2)
                )
                .build(2);

        page = mock(Page.class);
        when(page.getContent()).thenReturn(applicationStatistics);

        resource = mock(ApplicationCountSummaryPageResource.class);
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        when(applicationStatisticsRepositoryMock.findByCompetitionAndApplicationProcessActivityStateStateIn(eq(competitionId), eq(SUBMITTED_STATES), eq("filter"), argThat(new PageableMatcher(0, 20)))).thenReturn(page);
        when(applicationCountSummaryPageMapperMock.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationCountSummaryPageResource> result = service.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, ofNullable("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndInnovationArea() {
        when(applicationStatisticsRepositoryMock.findByCompetitionAndInnovationAreaProcessActivityStateStateIn(eq(competitionId), eq(1L), eq(SUBMITTED_STATES) , anyString(), eq(2L), argThat(new PageableMatcher(0, 20, srt("id", ASC ))))).thenReturn(page);
        when(applicationCountSummaryPageMapperMock.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationCountSummaryPageResource> result = service.getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionId, 1L,0, 20, ofNullable(2L), "", "");

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }
}
