package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.model.Question;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.form.service.FormInputService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFormSectionSaverTest {

    @InjectMocks
    private ApplicationFormSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private FormInputService formInputService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Test
    public void testAutoSaveApplicationFormSection() {
        Long questionId = 129192L;
        Question question = getQuestion();
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(question);

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.title", "New title", Optional.of(questionId));

        assertTrue(errors.isEmpty());
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveApplicationFormSectionErrors() {
        Long questionId = 129192L;
        Question question = getQuestion();
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(question);

        CompetitionResource competition = newCompetitionResource().build();
        List<Error> errors = service.autoSaveSectionField(competition, "notExisting", "Strange!@#1Value", Optional.of(questionId));

        assertTrue(!errors.isEmpty());
        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void testAutoSaveApplicationFormSectionEmptyQuestionIdErrors() {
        Long questionId = 129192L;
        Question question = getQuestion();
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(question);

        CompetitionResource competition = newCompetitionResource().build();
        List<Error> errors = service.autoSaveSectionField(competition, "question.title", "New title", Optional.empty());

        assertTrue(!errors.isEmpty());
        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    private Question getQuestion(){
        Question question = new Question();
        question.setId(10L);
        question.setMaxWords(400);
        question.setGuidance("Guidance test<p>with tags</p>");
        question.setGuidanceTitle("My lovely title");
        question.setTitle("Title");
        question.setSubTitle("Subtitle");
        question.setAppendix(Boolean.TRUE);
        question.setScored(Boolean.FALSE);
        return question;
    }

    public QuestionResource getQuestionResource() {
        QuestionResource questionResource = new QuestionResource();
        questionResource.setId(10L);
        questionResource.setName("Title");
        questionResource.setDescription("Subtitle");
        questionResource.setShortName("Shortname");
        return questionResource;
    }
}