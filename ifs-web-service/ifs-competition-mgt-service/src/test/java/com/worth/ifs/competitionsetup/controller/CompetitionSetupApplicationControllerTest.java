package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
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

import java.util.Collections;
import java.util.Optional;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static com.worth.ifs.competition.resource.CompetitionSetupSubsection.*;
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
    public void testGetEditCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/question/finance/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/finances"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testPostEditCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(FINANCES))).thenReturn(ServiceResult.serviceSuccess());
        final boolean fullApplicationFinance = true;
        final boolean includeGrowthTable = false;
        mockMvc.perform(post(URL_PREFIX + "/question/finance/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullApplicationFinance", String.valueOf(fullApplicationFinance))
                .param("includeGrowthTable", String.valueOf(includeGrowthTable)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        verify(competitionSetupService).saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(FINANCES));
    }

    @Test
    public void testViewCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/question/finance"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/finances"))
                .andExpect(model().attribute("editable", false));
    }

    @Test
    public void testApplicationProcessLandingPage() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.validateApplicationQuestions(eq(competition), any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/landing-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/"+COMPETITION_ID+"/section/application/landing-page"));
        verify(competitionSetupQuestionService).validateApplicationQuestions(eq(competition), any(), any());
    }

    @Test
    public void submitSectionApplicationQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));

        mockMvc.perform(post(URL_PREFIX +"/question")
                .param("question.questionId", questionId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

    }

    @Test
    public void submitSectionApplicationQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/question")
                .param("question.id", questionId.toString())
                .param("question.title", "My Title")
                .param("question.shortTitle", "Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX));

        verify(competitionSetupService).saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS));

    }

    @Test
    public void testGetEditCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/detail/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andExpect(model().attribute("editable", true));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testViewCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        ApplicationDetailsForm form = new ApplicationDetailsForm();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.getSubsectionFormData(
                competition,
                APPLICATION_FORM,
                CompetitionSetupSubsection.APPLICATION_DETAILS,
                Optional.empty())
                ).thenReturn(form);

        mockMvc.perform(get(URL_PREFIX + "/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andExpect(model().attribute("editable", false))
                .andExpect(model().attribute("competitionSetupForm", form));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testPostCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        final boolean useResubmissionQuestion = true;
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(APPLICATION_DETAILS))).thenReturn(ServiceResult.serviceSuccess());


        mockMvc.perform(post(URL_PREFIX + "/detail/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("useResubmissionQuestion", String.valueOf(useResubmissionQuestion)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        verify(competitionSetupService).saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(APPLICATION_DETAILS));

    }

    @Test
    public void testPostCompetitionApplicationDetailsWithError() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/detail/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("useResubmissionQuestion", String.valueOf("Invalid")))
                .andExpect(view().name("competition/application-details"));

    }

}