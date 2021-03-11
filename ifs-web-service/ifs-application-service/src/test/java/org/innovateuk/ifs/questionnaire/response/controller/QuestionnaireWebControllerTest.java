package org.innovateuk.ifs.questionnaire.response.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.form.QuestionnaireQuestionForm;
import org.innovateuk.ifs.questionnaire.response.populator.QuestionnaireQuestionViewModelPopulator;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.questionnaire.response.viewmodel.QuestionnaireQuestionViewModel;
import org.junit.Test;
import org.mockito.Mock;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.questionnaire.builder.QuestionnaireOptionResourceBuilder.newQuestionnaireOptionResource;
import static org.innovateuk.ifs.questionnaire.builder.QuestionnaireQuestionResponseResourceBuilder.newQuestionnaireQuestionResponseResource;
import static org.innovateuk.ifs.questionnaire.builder.QuestionnaireResourceBuilder.newQuestionnaireResource;
import static org.innovateuk.ifs.questionnaire.builder.QuestionnaireResponseResourceBuilder.newQuestionnaireResponseResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class QuestionnaireWebControllerTest extends BaseControllerMockMVCTest<QuestionnaireWebController> {

    @Mock
    private QuestionnaireRestService questionnaireRestService;

    @Mock
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Mock
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Mock
    private QuestionnaireResponseRestService questionnaireResponseRestService;

    @Mock
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;

    @Mock
    private QuestionnaireTextOutcomeRestService questionnaireTextOutcomeRestService;

    @Mock
    private QuestionnaireQuestionViewModelPopulator questionnaireQuestionViewModelPopulator;

    @Test
    public void welcomeScreen() throws Exception {
        UUID id = UUID.randomUUID();

        QuestionnaireResource questionnaire = newQuestionnaireResource().build();
        QuestionnaireResponseResource response = newQuestionnaireResponseResource()
                .withQuestionnaire(questionnaire.getId())
                .build();
        when(questionnaireResponseRestService.get(id.toString())).thenReturn(restSuccess(response));
        when(questionnaireRestService.get(questionnaire.getId())).thenReturn(restSuccess(questionnaire));

        mockMvc.perform(get("/questionnaire/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("questionnaire/welcome"))
                .andExpect(model().attributeExists("model"))
                .andReturn();

    }

    @Test
    public void start() throws Exception {
        UUID id = UUID.randomUUID();

        QuestionnaireResource questionnaire = newQuestionnaireResource()
                .withQuestions(newArrayList(99L))
                .build();
        QuestionnaireResponseResource response = newQuestionnaireResponseResource()
                .withQuestionnaire(questionnaire.getId())
                .build();
        when(questionnaireResponseRestService.get(id.toString())).thenReturn(restSuccess(response));
        when(questionnaireRestService.get(questionnaire.getId())).thenReturn(restSuccess(questionnaire));

        mockMvc.perform(post("/questionnaire/{id}", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/questionnaire/%s/question/%d", id.toString(), 99L)))
                .andReturn();
    }

    @Test
    public void question() throws Exception {
        UUID id = UUID.randomUUID();
        Long questionId = 1L;
        QuestionnaireQuestionResponseResource response = newQuestionnaireQuestionResponseResource()
                .withOption(2L)
                .build();
        QuestionnaireQuestionViewModel viewModel = mock(QuestionnaireQuestionViewModel.class);
        when(questionnaireQuestionResponseRestService.findByQuestionnaireQuestionIdAndQuestionnaireResponseId(questionId, id.toString()))
                .thenReturn(restSuccess(response));
        when(questionnaireQuestionViewModelPopulator.populate(id.toString(), questionId)).thenReturn(viewModel);

        mockMvc.perform(get("/questionnaire/{id}/question/{questionId}", id.toString(), questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("questionnaire/question"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("form", lambdaMatches(f -> ((QuestionnaireQuestionForm)f).getOption().equals(2L))))
                .andReturn();

    }

    @Test
    public void saveQuestionResponse() throws Exception {
        UUID id = UUID.randomUUID();
        Long questionId = 1L;
        Long nextQuestionId = 2L;

        QuestionnaireOptionResource option = newQuestionnaireOptionResource()
                .withDecisionType(DecisionType.QUESTION)
                .withDecision(nextQuestionId)
                .build();

        when(questionnaireOptionRestService.get(option.getId())).thenReturn(restSuccess(option));

        when(questionnaireQuestionResponseRestService.create(any())).thenReturn(restSuccess(new QuestionnaireQuestionResponseResource()));

        mockMvc.perform(post("/questionnaire/{id}/question/{questionId}", id.toString(), questionId)
                .param("option", String.valueOf(option.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/questionnaire/%s/question/%d", id.toString(), nextQuestionId)))
                .andReturn();

        verify(questionnaireQuestionResponseRestService).create(argThat(lambdaMatches(r -> r.getOption().equals(option.getId()))));
    }

    @Test
    public void outcome() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionnaireTextOutcomeResource textOutcome = new QuestionnaireTextOutcomeResource();
        textOutcome.setId(1L);
        textOutcome.setText("Finished");

        QuestionnaireResource questionnaire = newQuestionnaireResource().build();
        QuestionnaireResponseResource response = newQuestionnaireResponseResource()
                .withQuestionnaire(questionnaire.getId())
                .build();
        when(questionnaireResponseRestService.get(id.toString())).thenReturn(restSuccess(response));
        when(questionnaireRestService.get(questionnaire.getId())).thenReturn(restSuccess(questionnaire));

        when(questionnaireTextOutcomeRestService.get(textOutcome.getId())).thenReturn(restSuccess(textOutcome));

        mockMvc.perform(get("/questionnaire/{id}/outcome/{outcomeId}", id.toString(), textOutcome.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("questionnaire/outcome"))
                .andExpect(model().attribute("model", textOutcome))
                .andReturn();
    }

    @Override
    protected QuestionnaireWebController supplyControllerUnderTest() {
        return new QuestionnaireWebController();
    }
}