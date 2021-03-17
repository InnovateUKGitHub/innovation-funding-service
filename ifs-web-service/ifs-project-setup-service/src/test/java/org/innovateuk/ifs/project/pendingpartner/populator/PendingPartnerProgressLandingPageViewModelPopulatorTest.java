package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.PendingPartnerProgressLandingPageViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PendingPartnerProgressLandingPageViewModelPopulatorTest {

    @InjectMocks
    private PendingPartnerProgressLandingPageViewModelPopulator populator;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withIncludeJesForm(true)
                .withSubsidyControl(true)
                .build();
        ProjectResource project = newProjectResource()
                .withName("proj")
                .withApplication(3L)
                .withCompetition(competition.getId()).build();
        PendingPartnerProgressResource progress = newPendingPartnerProgressResource()
                .withYourFundingCompletedOn(ZonedDateTime.now())
                .withYourOrganisationCompletedOn(ZonedDateTime.now())
                .withTermsAndConditionsCompletedOn(ZonedDateTime.now())
                .withSubsidyBasisCompletedOn(ZonedDateTime.now())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(RESEARCH.getId())
                .build();
        QuestionResource question = newQuestionResource().build();

        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS)).thenReturn(restSuccess(question));
        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        PendingPartnerProgressLandingPageViewModel viewModel = populator.populate(project.getId(), organisation.getId());

        assertEquals("proj", viewModel.getProjectName());
        assertEquals(3L, viewModel.getApplicationId());
        assertEquals((long)project.getId(), viewModel.getProjectId());
        assertFalse( viewModel.isShowYourOrganisation());
        assertTrue(viewModel.isTermsAndConditionsComplete());
        assertTrue(viewModel.isYourFundingComplete());
        assertTrue(viewModel.isYourOrganisationComplete());
        assertTrue(viewModel.isSubsidyBasisComplete());
        assertEquals(question.getId(), viewModel.getSubsidyBasisQuestionId());
    }
}
