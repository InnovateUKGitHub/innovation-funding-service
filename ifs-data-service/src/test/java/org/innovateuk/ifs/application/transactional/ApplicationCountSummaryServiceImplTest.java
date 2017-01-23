package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.Role;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

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
        Role leadApplicationRole = newRole()
                .withType(LEADAPPLICANT)
                .build();
        List<Organisation> leadOrganisations = newOrganisation()
                .withId(1L, 2L)
                .withName("Lead Org 1", "Lead Org 2")
                .build(2);

        List<ApplicationStatistics> applicationStatistics = newApplicationStatistics()
                .withProcessRoles(
                        newProcessRole()
                                .withRole(leadApplicationRole)
                                .withOrganisationId(1L)
                                .build(1),
                        newProcessRole()
                                .withRole(leadApplicationRole)
                                .withOrganisationId(2L)
                                .build(1)
                )
                .build(2);

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource()
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .build(2);

        when(applicationStatisticsRepositoryMock.findByCompetition(competitionId)).thenReturn(applicationStatistics);
        when(applicationCountSummaryMapperMock.mapToResource(applicationStatistics.get(0))).thenReturn(summaryResources.get(0));
        when(applicationCountSummaryMapperMock.mapToResource(applicationStatistics.get(1))).thenReturn(summaryResources.get(1));
        when(organisationRepositoryMock.findAll(asList(1L, 2L))).thenReturn(leadOrganisations);

        List<ApplicationCountSummaryResource> result = service.getApplicationCountSummariesByCompetitionId(competitionId).getSuccessObject();

        InOrder inOrder = inOrder(applicationStatisticsRepositoryMock, applicationCountSummaryMapperMock, organisationRepositoryMock);
        inOrder.verify(applicationStatisticsRepositoryMock).findByCompetition(competitionId);
        inOrder.verify(organisationRepositoryMock).findAll(asList(1L, 2L));
        inOrder.verify(applicationCountSummaryMapperMock).mapToResource(applicationStatistics.get(0));
        inOrder.verify(applicationCountSummaryMapperMock).mapToResource(applicationStatistics.get(1));

        assertEquals(summaryResources, result);
    }
}
