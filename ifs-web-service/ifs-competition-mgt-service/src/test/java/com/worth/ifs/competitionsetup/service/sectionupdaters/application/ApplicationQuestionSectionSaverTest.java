package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        assertTrue(service.saveSection(competition, competitionSetupForm).isSuccess());
        verify(competitionSetupQuestionService).updateQuestion(question);
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(ApplicationQuestionForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

}