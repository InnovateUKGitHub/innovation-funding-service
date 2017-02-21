package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.Role;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ApplicationCountSummaryServiceImpl}
 */
public class ApplicationCountSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationCountSummaryService> {

    @Override
    protected ApplicationCountSummaryService supplyServiceUnderTest() {
        return new ApplicationCountSummaryServiceImpl();
    }

    private static class PageableMatcher extends ArgumentMatcher<Pageable> {
        private int expectedPage;
        private int expectedPageSize;

        public PageableMatcher(int expectedPage, int expectedPageSize) {
            this.expectedPage = expectedPage;
            this.expectedPageSize = expectedPageSize;
        }

        @Override
        public boolean matches(Object argument) {
            Pageable arg = (Pageable) argument;
            if (!(expectedPage == arg.getPageNumber())) {
                return false;
            }

            if (!(expectedPageSize == arg.getPageSize())) {
                return false;
            }

            return true;
        }
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        Long competitionId = 1L;
        Role leadApplicationRole = newRole()
                .withType(LEADAPPLICANT)
                .build();
        Role applicantRole = newRole()
                .withType(APPLICANT)
                .build();

        List<Organisation> leadOrganisations = newOrganisation()
                .withId(1L, 2L)
                .withName("Lead Org 1", "Lead Org 2")
                .build(2);

        List<ApplicationStatistics> applicationStatistics = newApplicationStatistics()
                .withProcessRoles(
                        newProcessRole()
                                .withRole(applicantRole, leadApplicationRole)
                                .withOrganisationId(2L, 1L)
                                .build(2),
                        newProcessRole()
                                .withRole(leadApplicationRole, applicantRole)
                                .withOrganisationId(2L, 3L)
                                .build(2)
                )
                .build(2);

        Page<ApplicationStatistics> page = mock(Page.class);
        when(page.getContent()).thenReturn(applicationStatistics);

        ApplicationCountSummaryPageResource resource = mock(ApplicationCountSummaryPageResource.class);

        when(applicationStatisticsRepositoryMock.findByCompetition(eq(competitionId),eq("filter"),argThat(new PageableMatcher(0,20)))).thenReturn(page);
        when(organisationRepositoryMock.findAll(asList(1L, 2L))).thenReturn(leadOrganisations);
        when(applicationCountSummaryPageMapperMock.mapToResource(page, leadOrganisations)).thenReturn(resource);

        ServiceResult<ApplicationCountSummaryPageResource> result = service.getApplicationCountSummariesByCompetitionId(competitionId,0,20, "filter");

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }
}
