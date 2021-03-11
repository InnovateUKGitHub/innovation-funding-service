package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTermsModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ProjectRestService projectRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;
    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @InjectMocks
    private ProjectTermsModelPopulator projectTermsModelPopulator;

    private CompetitionResource competition;
    private ProjectResource project;
    private OrganisationResource organisation;
    private PendingPartnerProgressResource pendingPartnerProgress;
    private ProjectFinanceResource projectFinanceResource;

    @Before
    public void setUp() {
        String termsTemplate = "terms-template";

        GrantTermsAndConditionsResource grantTermsAndConditions = newGrantTermsAndConditionsResource()
                .withName("Name")
                .withTemplate(termsTemplate)
                .withVersion(1)
                .build();

        String otherTermsTemplate = "other-terms-template";

        GrantTermsAndConditionsResource otherGrantTermsAndConditions = newGrantTermsAndConditionsResource()
                .withName("Name")
                .withTemplate(otherTermsTemplate)
                .withVersion(1)
                .build();

        competition = newCompetitionResource()
                .withId(1L)
                .withTermsAndConditions(grantTermsAndConditions)
                .withOtherFundingRulesTermsAndConditions(otherGrantTermsAndConditions)
                .build();

        project = newProjectResource()
                .withId(3L).withCompetition(competition.getId())
                .build();

        organisation = newOrganisationResource().withId(3L).build();
        pendingPartnerProgress = newPendingPartnerProgressResource().build();
        projectFinanceResource = newProjectFinanceResource()
                .withNorthernIrelandDeclarations(false)
                .build();

        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(pendingPartnerProgress));
        when(projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId())).thenReturn(restSuccess(projectFinanceResource));
    }

    @Test
    public void populate() {

        ProjectTermsViewModel result = projectTermsModelPopulator.populate(project.getId(), organisation.getId());

        assertEquals((long) project.getId(), result.getProjectId());
        assertEquals((long) organisation.getId(), result.getOrganisationId());
        assertEquals(competition.getTermsAndConditions().getTemplate(), result.getCompetitionTermsTemplate());
        assertEquals(pendingPartnerProgress.isTermsAndConditionsComplete(), result.isTermsAccepted());
        assertNull(pendingPartnerProgress.getTermsAndConditionsCompletedOn());

        InOrder inOrder = inOrder(projectRestService, organisationRestService, competitionRestService, pendingPartnerProgressRestService);
        inOrder.verify(projectRestService).getProjectById(project.getId());
        inOrder.verify(organisationRestService).getOrganisationById(organisation.getId());
        inOrder.verify(competitionRestService).getCompetitionById(project.getCompetition());
        inOrder.verify(pendingPartnerProgressRestService).getPendingPartnerProgress(project.getId(), organisation.getId());

        verifyNoMoreInteractions(projectRestService, organisationRestService, competitionRestService, pendingPartnerProgressRestService);
    }

    @Test
    public void populateWithOtherTerms() {

        projectFinanceResource.setNorthernIrelandDeclaration(true);

        ProjectTermsViewModel result = projectTermsModelPopulator.populate(project.getId(), organisation.getId());

        assertEquals((long) project.getId(), result.getProjectId());
        assertEquals((long) organisation.getId(), result.getOrganisationId());
        assertEquals(competition.getOtherFundingRulesTermsAndConditions().getTemplate(), result.getCompetitionTermsTemplate());
        assertEquals(pendingPartnerProgress.isTermsAndConditionsComplete(), result.isTermsAccepted());
        assertNull(pendingPartnerProgress.getTermsAndConditionsCompletedOn());

        InOrder inOrder = inOrder(projectRestService, organisationRestService, competitionRestService, pendingPartnerProgressRestService);
        inOrder.verify(projectRestService).getProjectById(project.getId());
        inOrder.verify(organisationRestService).getOrganisationById(organisation.getId());
        inOrder.verify(competitionRestService).getCompetitionById(project.getCompetition());
        inOrder.verify(pendingPartnerProgressRestService).getPendingPartnerProgress(project.getId(), organisation.getId());

        verifyNoMoreInteractions(projectRestService, organisationRestService, competitionRestService, pendingPartnerProgressRestService);
    }
}