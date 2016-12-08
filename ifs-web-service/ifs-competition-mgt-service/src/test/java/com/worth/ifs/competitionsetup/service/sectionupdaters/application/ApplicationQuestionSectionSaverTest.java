package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionSectionSaverTest {

    @InjectMocks
    private ApplicationQuestionSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.QUESTIONS, service.sectionToSave());
    }

    @Test
    public void testSaveCompetitionSetupSection() {
        ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(1L);
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));
        when(competitionSetupQuestionService.updateQuestion(question)).thenReturn(serviceSuccess());

        assertTrue(service.saveSection(competition, competitionSetupForm).isEmpty());
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setTitle("TitleOld");

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.title", "newTitle", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals("newTitle", question.getTitle());
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupAppendix() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.appendix", "yes", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getAppendix(), true);
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupAssessmentGuidance() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.assessmentGuidance", "assessmentGuidance", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getAssessmentGuidance(), "assessmentGuidance");
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupScored() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.scored", "yes", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getScored(), true);
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupWrittenFeedback() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.writtenFeedback", "yes", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getWrittenFeedback(), true);
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupScoreTotal() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.scoreTotal", "10", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getScoreTotal(), new Integer(10));
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupAssessmentMaxWords() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.assessmentMaxWords", "10", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getAssessmentMaxWords(), new Integer(10));
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testAutoSaveWordCount() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));
        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "question.maxWords", "-1", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getErrorKey(), "javax.validation.constraints.Min.message");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveShortTitleValidation() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));
        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "question.shortTitle", "", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "question.shortTitle");
        assertEquals(errors.get(0).getErrorKey(), "This field cannot be left blank");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowSubjectEmptyValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].subject", "", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].subject");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.subject.required");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowSubjectSizeValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].subject", RandomStringUtils.random(1025,"x"), Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].subject");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.subject.max");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowJustificationEmptyValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].justification", "", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].justification");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.justification.required");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowJustificationSizeValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].justification", RandomStringUtils.random(1025,"x"), Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].justification");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.justification.max");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowScoreFromValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].scoreFrom", "-1", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].scoreFrom");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.scorefrom.min");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }

    @Test
    public void testAutoSaveGuidanceRowScoreFromSuccess() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);
        question.setType(CompetitionSetupQuestionType.ASSESSED_QUESTION);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].scoreFrom", "1", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getGuidanceRows().get(0).getSubject(),"1,0");
        verify(competitionSetupQuestionService).getQuestion(1L);
    }

    @Test
    public void testAutoSaveGuidanceRowScoreToSuccess() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);
        question.setType(CompetitionSetupQuestionType.ASSESSED_QUESTION);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].scoreTo", "1", Optional.of(1L));

        assertTrue(errors.isEmpty());
        assertEquals(question.getGuidanceRows().get(0).getSubject(),"0,1");
        verify(competitionSetupQuestionService).getQuestion(1L);
    }

    @Test
    public void testAutoSaveGuidanceRowScoreToValidation() {

        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        List<GuidanceRowResource> guidanceRowResources = newFormInputGuidanceRowResourceBuilder().build(1);
        question.setGuidanceRows(guidanceRowResources);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        List<Error> errors = service.autoSaveSectionField(competition, "guidanceRow[0].scoreTo", "-1", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0).getFieldName(), "guidanceRow[0].scoreTo");
        assertEquals(errors.get(0).getErrorKey(), "validation.applicationquestionform.scoreto.min");
        verify(competitionSetupQuestionService).getQuestion(1L);
        verifyNoMoreInteractions(competitionSetupQuestionService);
    }


    @Test
    public void testAutoSaveCompetitionSetupSectionErrors() {
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setMaxWords(400);

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, "question.maxWords", "Hihi", Optional.of(1L));

        assertFalse(errors.isEmpty());
        assertEquals(Integer.valueOf(400), question.getMaxWords());
        verify(competitionSetupQuestionService, never()).updateQuestion(question);
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionUnknown() {
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setTitle("TitleOld");

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));

        List<Error> errors = service.autoSaveSectionField(competition, "notExisting", "Strange!@#1Value", Optional.of(1L));

        assertTrue(!errors.isEmpty());
        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionEmptyObjectId() {
        CompetitionResource competition = newCompetitionResource().build();
        List<Error> errors = service.autoSaveSectionField(competition, "notExisting", "Strange!@#1Value", Optional.empty());

        assertTrue(!errors.isEmpty());
        verify(competitionService, never()).update(competition);
    }
}