package org.innovateuk.ifs.management.competition.setup.projectimpact.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.projectimpact.form.ProjectImpactForm;
import org.innovateuk.ifs.management.competition.setup.projectimpact.populator.ProjectImpactFormPopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_IMPACT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SupportingDocumentControllerTest extends BaseControllerMockMVCTest<SupportingDocumentController> {

    private static final String URL = "/competition/setup/{competitionId}/section/project-impact";
    private CompetitionResource competition;
    private long competitionId;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;


    @Override
    protected SupportingDocumentController supplyControllerUnderTest() {
        return new SupportingDocumentController(competitionRestService, competitionSetupService,
                competitionApplicationConfigRestService, competitionSetupRestService);
    }

    @Before
    public void setup() {
        competitionId = 100L;
        competition = newCompetitionResource().withId(competitionId).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
    }

    @Test
    public void supportingDocumentDetails() throws Exception {
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)).thenReturn(true);
        when(competitionSetupService.getSectionFormPopulator(PROJECT_IMPACT)).thenReturn(mock(ProjectImpactFormPopulator.class));
        mockMvc.perform(get(URL, competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
    }

    @Test
    public void submitSupportingDocumentDetails() throws Exception {

        ProjectImpactForm projectImpactForm = new ProjectImpactForm();
        projectImpactForm.setProjectImpactSurveyApplicable(Boolean.TRUE);

        CompetitionApplicationConfigResource configResource = new CompetitionApplicationConfigResource();
        configResource.setImSurveyRequired(true);

            when(competitionApplicationConfigRestService.findOneByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));
            when(competitionApplicationConfigRestService.updateImpactSurvey(competitionId, configResource)).thenReturn(restSuccess(configResource));

        mockMvc.perform(post(URL, competitionId)
                        .param("projectImpactSurveyApplicable", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/setup/%d/section/%s?model.general.editable=false", competition.getId(), PROJECT_IMPACT.getPostMarkCompletePath())));
    }


}