package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.service.populator.CompetitionSetupPopulator;
import org.innovateuk.ifs.competitionsetup.viewmodel.QuestionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.ASSESSED_QUESTION;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.SCOPE;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
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
    private static final Long COMPETITION_ID = 12L;
    private static final Long QUESTION_ID = 1L;
    private static final String URL_PREFIX = "/competition/setup/"+COMPETITION_ID+"/section/application";
    private static final CompetitionResource UNEDITABLE_COMPETITION = newCompetitionResource()
            .withCompetitionStatus(CompetitionStatus.OPEN)
            .withSetupComplete(true)
            .withStartDate(ZonedDateTime.now().minusDays(1))
            .withFundersPanelDate(ZonedDateTime.now().plusDays(1)).build();

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Mock
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Override
    protected CompetitionSetupApplicationController supplyControllerUnderTest() { return new CompetitionSetupApplicationController(); }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);
    }

    @Test
    public void testGetEditCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/question/finance/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/finances"));

        verify(competitionService, never()).update(competition);
        verify(competitionService).setSetupSectionMarkedAsIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
    }

    @Test
    public void testGetEditCompetitionFinanceRedirect() throws Exception {
        when(competitionService.getById(COMPETITION_ID)).thenReturn(UNEDITABLE_COMPETITION);

        mockMvc.perform(get(URL_PREFIX + "/question/finance/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(competitionService, never()).update(UNEDITABLE_COMPETITION);
    }

    @Test
    public void testPostEditCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(FINANCES)))
                .thenReturn(ServiceResult.serviceSuccess());

        final boolean fullApplicationFinance = true;
        final boolean includeGrowthTable = false;
        final String fundingRules = "Funding rules for this competition";

        mockMvc.perform(post(URL_PREFIX + "/question/finance/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullApplicationFinance", String.valueOf(fullApplicationFinance))
                .param("includeGrowthTable", String.valueOf(includeGrowthTable))
                .param("fundingRules", String.valueOf(fundingRules)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        verify(competitionSetupService).saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(FINANCES));
    }

    @Test
    public void testViewCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupPopulator.populateGeneralModelAttributes(competition, CompetitionSetupSection.APPLICATION_FORM))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.FALSE));

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/question/finance"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/finances"))
                .andReturn().getModelAndView().getModelMap();

        assertEquals(QuestionSetupViewModel.class, model.get("model").getClass());
        QuestionSetupViewModel viewModel = (QuestionSetupViewModel) model.get("model");

        assertEquals(Boolean.FALSE, viewModel.isEditable());
    }

    @Test
    public void testApplicationProcessLandingPage() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
        ArgumentCaptor<Model> model = ArgumentCaptor.forClass(Model.class);
        ArgumentCaptor<CompetitionResource> competitionResource = ArgumentCaptor.forClass(CompetitionResource.class);
        ArgumentCaptor<CompetitionSetupSection> competitionSetupSection = ArgumentCaptor.forClass(CompetitionSetupSection.class);
        verify(competitionSetupService, atLeastOnce()).populateCompetitionSectionModelAttributes(competitionResource.capture(), competitionSetupSection.capture());
        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSetApplicationProcessAsComplete() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.validateApplicationQuestions(eq(competition), any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/landing-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/"+COMPETITION_ID+"/section/application/landing-page"));
        verify(competitionSetupQuestionService).validateApplicationQuestions(eq(competition), any(), any());
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(ASSESSED_QUESTION);

        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX +"/question/" + questionId + "/edit")
                .param("question.type", ASSESSED_QUESTION.name())
                .param("question.questionId", questionId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(SCOPE);
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX +"/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.type", SCOPE.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithGuidanceRowErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(ASSESSED_QUESTION);
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));
        when(competitionSetupPopulator.populateGeneralModelAttributes(any(CompetitionResource.class), any(CompetitionSetupSection.class)))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.TRUE));

        Map<String, Object> model = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.type", ASSESSED_QUESTION.name())
                .param("question.questionId", questionId.toString())
                .param("question.title", "My Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400")
                .param("question.appendix", "true")
                .param("question.scored", "true")
                .param("question.scoreTotal", "100")
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidance", "My assessment guidance")
                .param("question.assessmentGuidanceTitle", "My assessment guidance title")
                .param("question.assessmentMaxWords", "200")
                .param("question.type", "")
                .param("guidanceRows[0].scoreFrom", "")
                .param("guidanceRows[0].scoreTo", "")
                .param("guidanceRows[0].justification", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"))
                .andReturn().getModelAndView().getModel();

        assertEquals(QuestionSetupViewModel.class, model.get("model").getClass());
        QuestionSetupViewModel viewModel = (QuestionSetupViewModel) model.get("model");

        assertEquals(Boolean.TRUE, viewModel.getGeneral().isEditable());

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithGuidanceRowErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(SCOPE);
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.type", SCOPE.name())
                .param("question.questionId", questionId.toString())
                .param("question.title", "My Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400")
                .param("question.appendix", "true")
                .param("question.scored", "true")
                .param("question.scoreTotal", "100")
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidance", "My assessment guidance")
                .param("question.assessmentGuidanceTitle", "My assessment guidance title")
                .param("question.assessmentMaxWords", "200")
                .param("question.type", "Scope")
                .param("guidanceRows[0].subject", "")
                .param("guidanceRows[0].justification", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(ASSESSED_QUESTION);

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX + "/question/" + questionId.toString() + "/edit")
                .param("question.type", ASSESSED_QUESTION.name())
                .param("question.questionId", questionId.toString())
                .param("question.title", "My Title")
                .param("question.shortTitle", "My Short Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400")
                .param("question.appendix", "true")
                .param("question.scored", "true")
                .param("question.scoreTotal", "100")
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidance", "My assessment guidance")
                .param("question.assessmentGuidanceTitle", "My assessment guidance title")
                .param("question.assessmentMaxWords", "200")
                .param("guidanceRows[0].scoreFrom", "1")
                .param("guidanceRows[0].scoreTo", "10")
                .param("guidanceRows[0].justification", "My justification"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        verify(competitionSetupService).saveCompetitionSetupSubsection(isA(ApplicationQuestionForm.class),
                eq(competition),
                eq(CompetitionSetupSection.APPLICATION_FORM), eq(CompetitionSetupSubsection.QUESTIONS));
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithCheckedOptionsShouldResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));
        when(competitionSetupPopulator.populateGeneralModelAttributes(any(CompetitionResource.class), any(CompetitionSetupSection.class)))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.TRUE));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidanceTitle", "")
                .param("question.scored", "true")
                .param("question.scoreTotal", "")
                .param("question.type", "ASSESSED_QUESTION")
                .param("guidanceRows[0].scoreFrom", "")
                .param("guidanceRows[0].scoreTo", "")
                .param("guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);
        assertEquals(QuestionSetupViewModel.class, result.getModelAndView().getModel().get("model").getClass());
        QuestionSetupViewModel viewModel = (QuestionSetupViewModel)result.getModelAndView().getModel().get("model");

        assertEquals(Boolean.TRUE, viewModel.getGeneral().isEditable());

        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.scoreTotal").getCode());
        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.assessmentGuidanceTitle").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("guidanceRows[0].justification").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("guidanceRows[0].scoreTo").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("guidanceRows[0].scoreFrom").getCode());
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithUncheckedOptionsShouldNotResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenGuidance", "false")
                .param("question.guidanceTitle", "")
                .param("question.scored", "false")
                .param("question.scoreTotal", "")
                .param("question.type", "ASSESSED_QUESTION")
                .param("guidanceRows[0].scoreFrom", "")
                .param("guidanceRows[0].scoreTo", "")
                .param("guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);

        assertNull(bindingResult.getFieldError("question.scoreTotal"));
        assertNull(bindingResult.getFieldError("question.assessmentGuidanceTitle"));
        assertNull(bindingResult.getFieldError("guidanceRows[0].justification"));
        assertNull(bindingResult.getFieldError("guidanceRows[0].scoreTo"));
        assertNull(bindingResult.getFieldError("guidanceRows[0].scoreFrom"));
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithCheckedOptionsShouldResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));
        when(competitionSetupPopulator.populateGeneralModelAttributes(any(CompetitionResource.class), any(CompetitionSetupSection.class)))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.TRUE));


        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidanceTitle", "")
                .param("question.scored", "true")
                .param("question.scoreTotal", "")
                .param("question.guidanceRows[0].subject", "")
                .param("question.guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);
        Map<String, Object> model = result.getModelAndView().getModel();

        assertEquals(QuestionSetupViewModel.class, model.get("model").getClass());
        QuestionSetupViewModel viewModel = (QuestionSetupViewModel) model.get("model");

        assertEquals(Boolean.TRUE, viewModel.isEditable());

        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.scoreTotal").getCode());
        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.assessmentGuidanceTitle").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("question.guidanceRows[0].justification").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("question.guidanceRows[0].subject").getCode());
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithUncheckedOptionsShouldNotResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));
        when(competitionSetupPopulator.populateGeneralModelAttributes(competition, CompetitionSetupSection.APPLICATION_FORM))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.TRUE));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenGuidance", "false")
                .param("question.guidanceTitle", "")
                .param("question.scored", "false")
                .param("question.scoreTotal", "")
                .param("question.guidanceRows[0].subject", "")
                .param("question.guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);

        Map<String, Object> model = result.getModelAndView().getModel();
        assertEquals(QuestionSetupViewModel.class, model.get("model").getClass());

        QuestionSetupViewModel viewModel = (QuestionSetupViewModel) model.get("model");
        assertEquals(Boolean.TRUE, viewModel.isEditable());

        assertNull(bindingResult.getFieldError("question.scoreTotal"));
        assertNull(bindingResult.getFieldError("question.assessmentGuidanceTitle"));
        assertNull(bindingResult.getFieldError("question.guidanceRows[0].justification"));
        assertNull(bindingResult.getFieldError("quesiton.guidanceRows[0].subject"));
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(PROJECT_DETAILS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.type", SCOPE.name())
                .param("question.questionId", questionId.toString())
                .param("question.title", "My Title")
                .param("question.shortTitle", "Title")
                .param("question.guidanceTitle", "My Title")
                .param("question.guidance", "My guidance")
                .param("question.maxWords", "400")
                .param("question.appendix", "true")
                .param("question.scored", "true")
                .param("question.scoreTotal", "100")
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidance", "My assessment guidance")
                .param("question.assessmentGuidanceTitle", "My assessment guidance title")
                .param("question.assessmentMaxWords", "200")
                .param("question.guidanceRows[0].subject", "YES")
                .param("question.guidanceRows[0].justification", "My justification")
                .param("question.guidanceRows[1].subject", "NO")
                .param("question.guidanceRows[1].justification", "My justification"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        verify(competitionSetupService).saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(PROJECT_DETAILS));
    }

    @Test
    public void testGetEditCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupPopulator.populateGeneralModelAttributes(competition, CompetitionSetupSection.APPLICATION_FORM))
                .thenReturn(getBasicGeneralViewModel(CompetitionSetupSection.APPLICATION_FORM, competition, Boolean.TRUE));

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "/detail/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();
        assertTrue(model.containsAttribute("model"));
        assertEquals(model.get("model").getClass(), QuestionSetupViewModel.class);
        QuestionSetupViewModel viewModel = (QuestionSetupViewModel) model.get("model");

        assertEquals(Boolean.TRUE, viewModel.getGeneral().isEditable());
        assertEquals(CompetitionSetupSection.APPLICATION_FORM, viewModel.getGeneral().getCurrentSection());

        verify(competitionService, never()).update(competition);
        verify(competitionService).setSetupSectionMarkedAsIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
    }

    private GeneralSetupViewModel getBasicGeneralViewModel(CompetitionSetupSection section, CompetitionResource competition, Boolean editable) {
        return new GeneralSetupViewModel(editable, competition, section, CompetitionSetupSection.values(), Boolean.TRUE);
    }

    @Test
    public void testGetEditCompetitionApplicationDetailsRedirect() throws Exception {
        when(competitionService.getById(COMPETITION_ID)).thenReturn(UNEDITABLE_COMPETITION);

        mockMvc.perform(get(URL_PREFIX + "/detail/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(competitionService, never()).update(UNEDITABLE_COMPETITION);
    }

    @Test
    public void testViewCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();
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
                .andExpect(model().attribute("competitionSetupForm", form));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testPostCompetitionApplicationDetails() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

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
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withSectionSetupStatus(asMap(CompetitionSetupSection.INITIAL_DETAILS, true))
                .build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/detail/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("useResubmissionQuestion", String.valueOf("Invalid")))
                .andExpect(view().name("competition/application-details"));
    }

    @Test
    public void testGetEditCompetitionQuestion() throws Exception {

        CompetitionResource competition = newCompetitionResource().withId(QUESTION_ID).withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withType(CompetitionSetupQuestionType.ASSESSED_QUESTION).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(QUESTION_ID)).thenReturn(ServiceResult.serviceSuccess(question));

        mockMvc.perform(get(URL_PREFIX + "/question/" + QUESTION_ID + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, atLeastOnce()).getQuestion(QUESTION_ID);
        verify(competitionService, never()).update(competition);
        verify(competitionService).setSetupSectionMarkedAsIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
    }

    @Test
    public void testGetEditCompetitionQuestionRedirect() throws Exception {
        when(competitionService.getById(COMPETITION_ID)).thenReturn(UNEDITABLE_COMPETITION);

        mockMvc.perform(get(URL_PREFIX + "/question/" + QUESTION_ID + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(competitionSetupQuestionService, never()).getQuestion(QUESTION_ID);
        verify(competitionService, never()).update(UNEDITABLE_COMPETITION);
    }

    @Test
    public void createQuestion() throws Exception {
        CompetitionSetupQuestionResource competitionSetupQuestionResource = newCompetitionSetupQuestionResource().withQuestionId(10L).build();

        when(competitionSetupQuestionService.createDefaultQuestion(COMPETITION_ID)).thenReturn(serviceSuccess(competitionSetupQuestionResource));

        mockMvc.perform(post(URL_PREFIX + "/landing-page")
                .param("createQuestion", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + URL_PREFIX + "/question/10/edit"));

        verify(competitionSetupQuestionService).createDefaultQuestion(COMPETITION_ID);
    }

    @Test
    public void createQuestion_serviceErrorResultsInInternalServerErrorResponse() throws Exception {
        Error error = Error.globalError("Something is wrong.");

        when(competitionSetupQuestionService.createDefaultQuestion(COMPETITION_ID)).thenReturn(serviceFailure(error));

        mockMvc.perform(post(URL_PREFIX + "/landing-page")
                .param("createQuestion", "true"))
                .andExpect(status().is5xxServerError());

        verify(competitionSetupQuestionService).createDefaultQuestion(COMPETITION_ID);
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        Long questionId = 1L;

        CompetitionResource competition = newCompetitionResource().build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.deleteQuestion(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/landing-page")
                .param("deleteQuestion", questionId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:" + URL_PREFIX + "/landing-page"));

        verify(competitionSetupQuestionService).deleteQuestion(questionId);
    }
}
