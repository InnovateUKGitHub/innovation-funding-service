package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.controller.CompetitionSetupApplicationController;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionSetupApplicationController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupApplicationControllerTest extends BaseControllerMockMVCTest<CompetitionSetupApplicationController> {

    private static final Long COMPETITION_ID = Long.valueOf(12);
    private static final String URL_PREFIX = "/competition/setup/"+COMPETITION_ID+"/section/application";

    @Mock
    private CategoryService categoryService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Mock
    private Validator validator;

    @Override
    protected CompetitionSetupApplicationController supplyControllerUnderTest() { return new CompetitionSetupApplicationController(); }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGetCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/question/finance"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/finances"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testPostCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        final boolean fullApplicationFinance = true;
        final boolean includeGrowthTable = false;
        mockMvc.perform(post(URL_PREFIX + "/question/finance")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullApplicationFinance", String.valueOf(fullApplicationFinance))
                .param("includeGrowthTable", String.valueOf(includeGrowthTable)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        ArgumentCaptor<CompetitionResource> argument = ArgumentCaptor.forClass(CompetitionResource.class);
        verify(competitionService).update(argument.capture());
        assertThat(argument.getValue().isFullApplicationFinance(), equalTo(fullApplicationFinance));
        assertThat(argument.getValue().isIncludeGrowthTable(), equalTo(includeGrowthTable));
    }

    @Test
    public void testApplicationProcessLandingPage() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
        ArgumentCaptor<Model> model = ArgumentCaptor.forClass(Model.class);
        ArgumentCaptor<CompetitionResource> competitionResource = ArgumentCaptor.forClass(CompetitionResource.class);
        ArgumentCaptor<CompetitionSetupSection> competitionSetupSection = ArgumentCaptor.forClass(CompetitionSetupSection.class);
        verify(competitionSetupService, atLeastOnce()).populateCompetitionSectionModelAttributes(model.capture(), competitionResource.capture(), competitionSetupSection.capture());
        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSetApplicationProcessAsComplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/mark-as-complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/"+COMPETITION_ID+"/section/application/landing-page"));
        verify(competitionService, atMost(1)).update(competition);
    }

    @Test
    public void submitSectionApplicationQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        QuestionViewModel question = new QuestionViewModel();
        question.setId(questionId);

        mockMvc.perform(post(URL_PREFIX +"/question")
                .param("question.id", questionId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;

        mockMvc.perform(post(URL_PREFIX + "/question")
                .param("question.id", questionId.toString())
                .param("question.title", "My Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX));

        verify(competitionSetupQuestionService).updateQuestion(isA(QuestionViewModel.class));
    }

    @Test
    public void testGetEditCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/detail/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andExpect(model().attribute("editable", true));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testViewCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andExpect(model().attribute("editable", false));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testPostCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        final boolean useProjectTitleQuestion = true;
        final boolean useDurationQuestion = true;
        final boolean useResubmissionQuestion = true;
        final boolean estimatedStartDateQuestion = false;

        mockMvc.perform(post(URL_PREFIX + "/detail")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("useProjectTitleQuestion", String.valueOf(useProjectTitleQuestion))
                .param("useDurationQuestion", String.valueOf(useDurationQuestion))
                .param("useResubmissionQuestion", String.valueOf(useResubmissionQuestion))
                .param("estimatedStartDateQuestion", String.valueOf(estimatedStartDateQuestion)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        ArgumentCaptor<CompetitionResource> argument = ArgumentCaptor.forClass(CompetitionResource.class);
        verify(competitionService).update(argument.capture());
        assertThat(argument.getValue().isUseProjectTitleQuestion(), equalTo(useProjectTitleQuestion));
        assertThat(argument.getValue().isUseDurationQuestion(), equalTo(useDurationQuestion));
        assertThat(argument.getValue().isUseResubmissionQuestion(), equalTo(useResubmissionQuestion));
        assertThat(argument.getValue().isUseEstimatedStartDateQuestion(), equalTo(estimatedStartDateQuestion));
    }

}