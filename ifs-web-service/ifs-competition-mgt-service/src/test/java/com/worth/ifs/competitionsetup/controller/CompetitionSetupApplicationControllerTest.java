package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
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

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    }

    @Test
    public void testPostEditCompetitionFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        final boolean fullApplicationFinance = true;
        final boolean includeGrowthTable = false;
        mockMvc.perform(post(URL_PREFIX + "/question/finance/edit")
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

        mockMvc.perform(get(URL_PREFIX + "/mark-as-complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/"+COMPETITION_ID+"/section/application/landing-page"));
        verify(competitionService, atMost(1)).update(competition);
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);

        mockMvc.perform(post(URL_PREFIX +"/question?ASSESSED_QUESTION=true")
                .param("question.questionId", questionId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationScopeQuestionWithErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);

        mockMvc.perform(post(URL_PREFIX +"/question?SCOPE=true")
                .param("question.questionId", questionId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/question"));

        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithGuidanceRowErrors() throws Exception {
        Long questionId = 4L;
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(questionId);

        mockMvc.perform(post(URL_PREFIX + "/question?ASSESSED_QUESTION=true")
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

        mockMvc.perform(post(URL_PREFIX + "/question?ASSESSED_QUESTION=true")
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
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/question?ASSESSED_QUESTION=true")
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
                .andExpect(redirectedUrl(URL_PREFIX));

        verify(competitionSetupService).saveCompetitionSetupSubsection(isA(ApplicationQuestionForm.class),
                eq(competition),
                eq(CompetitionSetupSection.APPLICATION_FORM), eq(CompetitionSetupSubsection.QUESTIONS));
    }

    @Test
    public void submitSectionApplicationAssessedQuestionWithCheckedOptionsShouldResultInError() throws Exception {
        Long questionId = 4L;
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question?ASSESSED_QUESTION=true")
                .param("question.questionId", questionId.toString())
                .param("question.writtenFeedback", "true")
                .param("question.assessmentGuidanceTitle", "")
                .param("question.scored", "true")
                .param("question.scoreTotal", "")
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
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question?ASSESSED_QUESTION=true")
                .param("question.questionId", questionId.toString())
                .param("question.writtenGuidance", "false")
                .param("question.guidanceTitle", "")
                .param("question.scored", "false")
                .param("question.scoreTotal", "")
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
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question?SCOPE=true")
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/question?SCOPE=true")
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

        mockMvc.perform(post(URL_PREFIX + "/question?SCOPE=true")
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
                .andExpect(redirectedUrl(URL_PREFIX));

        verify(competitionSetupQuestionService).updateQuestion(isA(CompetitionSetupQuestionResource.class));
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
                CompetitionSetupSection.APPLICATION_FORM,
                CompetitionSetupSubsection.APPLICATION_DETAILS,
                null)
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

        mockMvc.perform(post(URL_PREFIX + "/detail/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("useResubmissionQuestion", String.valueOf(useResubmissionQuestion)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/landing-page"));

        ArgumentCaptor<CompetitionResource> argument = ArgumentCaptor.forClass(CompetitionResource.class);
        verify(competitionService).update(argument.capture());
        assertThat(argument.getValue().isUseResubmissionQuestion(), equalTo(useResubmissionQuestion));
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