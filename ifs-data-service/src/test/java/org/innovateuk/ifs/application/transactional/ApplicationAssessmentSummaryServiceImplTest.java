package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.mockito.InOrder;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class ApplicationAssessmentSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationAssessmentSummaryServiceImpl> {

    @Override
    protected ApplicationAssessmentSummaryServiceImpl supplyServiceUnderTest() {
        return new ApplicationAssessmentSummaryServiceImpl();
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Organisation org1 = buildOrganisationWithName("Acme Ltd.");
        Organisation org2 = buildOrganisationWithName("IO systems");
        Organisation org3 = buildOrganisationWithName("Liquid Dynamics");
        Application application = newApplication()
                .withName("Progressive machines")
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, LEADAPPLICANT, COMP_ADMIN)
                        .withOrganisation(org1, org2, org3)
                        .buildArray(3, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"))
                .build();

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(organisationRepositoryMock.findOne(org1.getId())).thenReturn(org1);
        when(organisationRepositoryMock.findOne(org2.getId())).thenReturn(org2);
        when(organisationRepositoryMock.findOne(org3.getId())).thenReturn(org3);

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccessObjectOrThrowException();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private Organisation buildOrganisationWithName(String name) {
        return newOrganisation()
                .withName(name)
                .build();
    }
}