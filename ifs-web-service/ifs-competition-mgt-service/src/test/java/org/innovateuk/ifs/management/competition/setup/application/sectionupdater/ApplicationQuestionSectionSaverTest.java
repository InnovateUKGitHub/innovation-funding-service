package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationQuestionSectionSaverTest {

    @InjectMocks
    private QuestionSectionUpdater service;

    @Mock
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.QUESTIONS, service.subsectionToSave());
    }

    @Test
    public void saveSection() {
        QuestionForm competitionSetupForm = new QuestionForm();
        competitionSetupForm.setNumberOfUploads(0);
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();

        question.setQuestionId(1L);
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(questionSetupCompetitionRestService.getByQuestionId(1L)).thenReturn(restSuccess(question));
        when(questionSetupCompetitionRestService.save(question)).thenReturn(restSuccess());

        assertTrue(service.saveSection(competition, competitionSetupForm).isSuccess());
        verify(questionSetupCompetitionRestService).save(question);
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(QuestionForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

}
