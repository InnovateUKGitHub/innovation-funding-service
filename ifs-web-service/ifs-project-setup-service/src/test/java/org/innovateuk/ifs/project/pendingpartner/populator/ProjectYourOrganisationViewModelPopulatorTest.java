package org.innovateuk.ifs.project.pendingpartner.populator;

import java.time.ZonedDateTime;
import java.util.List;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectYourOrganisationViewModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @InjectMocks
    private YourOrganisationViewModelPopulator yourOrganisationViewModelPopulator;

    @Test
    public void populate() {
        long projectId = 1L;
        long organisationId = 2L;

        CompetitionResource competition = newCompetitionResource()
            .withFundingType(FundingType.GRANT)
            .withIncludeJesForm(true)
            .withCompetitionTypeName("Horizon 2020")
            .build();
        ProjectResource project = newProjectResource()
            .withId(projectId)
            .withName("proj")
            .withApplication(3L)
            .withCompetition(competition.getId()).build();
        PendingPartnerProgressResource progress = newPendingPartnerProgressResource()
            .withYourFundingCompletedOn(ZonedDateTime.now())
            .withYourOrganisationCompletedOn(ZonedDateTime.now())
            .withTermsAndConditionsCompletedOn(ZonedDateTime.now())
            .build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(yourOrganisationRestService.isShowStateAidAgreement(projectId, organisationId)).thenReturn(serviceSuccess(true));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId)).thenReturn(restSuccess(progress));

        ProjectYourOrganisationViewModel actual = yourOrganisationViewModelPopulator.populate(projectId,
            organisationId);

        List<FormOption> expectedOrgSizeOptions = simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));

        assertEquals(expectedOrgSizeOptions, actual.getOrganisationSizeOptions());
        assertTrue(actual.isH2020());
        assertTrue(actual.isShowOrganisationSizeAlert());
        assertTrue(actual.isShowStateAidAgreement());
        assertTrue(actual.isShowHints());
        assertEquals(organisationId, actual.getOrganisationId());
        assertEquals(projectId, actual.getProjectId());
        assertEquals("proj", actual.getProjectName());
        assertTrue(actual.isReadOnly());

    }
}
