package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.ASSESSED_QUESTION;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.SCOPE;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    private static final Long QUESTION_ID = Long.valueOf(1);
    private static final String URL_PREFIX = "/competition/setup/"+COMPETITION_ID+"/section/application";
    private static final CompetitionResource UNEDITABLE_COMPETITION = newCompetitionResource()
            .withCompetitionStatus(CompetitionStatus.OPEN)
            .withSetupComplete(true)
            .withStartDate(LocalDateTime.now().minusDays(1))
            .withFundersPanelDate(LocalDateTime.now().plusDays(1)).build();

    @Mock
    private CategoryService categoryService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

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
    public void submitSectionApplicationAssessedQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
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
                .andExpect(model().attribute("editable", true))
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithGuidanceRowErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(SCOPE);
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
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
                .andExpect(model().attribute("editable", true))
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);
        question.setType(ASSESSED_QUESTION);
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSubsection(any(CompetitionSetupForm.class), eq(competition), eq(APPLICATION_FORM), eq(QUESTIONS))).thenReturn(serviceFailure(Collections.emptyList()));
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

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
                .andExpect(model().attribute("editable", true))
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);

        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.scoreTotal").getCode());
        assertEquals("FieldRequiredIf", bindingResult.getFieldError("question.assessmentGuidanceTitle").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("guidanceRows[0].justification").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("guidanceRows[0].scoreTo").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("guidanceRows[0].scoreFrom").getCode());
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithUncheckedOptionsShouldNotResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
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
                .andExpect(model().attribute("editable", true))
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(questionId)
                .withType(ASSESSED_QUESTION).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidanceTitle", "")
                .param("question.scored", "true")
                .param("question.scoreTotal", "")
                .param("question.guidanceRows[0].subject", "")
                .param("question.guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("editable", true))
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);

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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(question));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question/" + questionId + "/edit")
                .param("question.questionId", questionId.toString())
                .param("question.writtenGuidance", "false")
                .param("question.guidanceTitle", "")
                .param("question.scored", "false")
                .param("question.scoreTotal", "")
                .param("question.guidanceRows[0].subject", "")
                .param("question.guidanceRows[0].justification", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("editable", true))
                .andReturn();

        BindingResult bindingResult = (BindingResult)result.getModelAndView().getModel().get("org.springframework.validation.BindingResult."+CompetitionSetupController.COMPETITION_SETUP_FORM_KEY);

        assertNull(bindingResult.getFieldError("question.scoreTotal"));
        assertNull(bindingResult.getFieldError("question.assessmentGuidanceTitle"));
        assertNull(bindingResult.getFieldError("question.guidanceRows[0].justification"));
        assertNull(bindingResult.getFieldError("quesiton.guidanceRows[0].subject"));
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithoutErrors() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/detail/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/application-details"))
                .andExpect(model().attribute("editable", true));

        verify(competitionService, never()).update(competition);
        verify(competitionService).setSetupSectionMarkedAsIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
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

        verify(competitionService, never()).update(competition);
        verify(competitionService).setSetupSectionMarkedAsIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
    }

    @Test
    public void testGetEditCompetitionQuestionRedirect() throws Exception {
        when(competitionService.getById(COMPETITION_ID)).thenReturn(UNEDITABLE_COMPETITION);

        mockMvc.perform(get(URL_PREFIX + "/question/" + QUESTION_ID + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(competitionService, never()).update(UNEDITABLE_COMPETITION);
    }
}
