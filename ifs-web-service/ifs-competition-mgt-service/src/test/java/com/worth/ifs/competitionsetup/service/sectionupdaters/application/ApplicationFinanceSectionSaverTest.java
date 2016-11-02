package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private ApplicationFinanceSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.FINANCES, service.sectionToSave());
    }

    @Test
    public void testSaveCompetitionSetupSection() {
        ApplicationFinanceForm competitionSetupForm = new ApplicationFinanceForm();

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        service.saveSection(competition, competitionSetupForm);
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        competitionResource.setFullApplicationFinance(FALSE);

        List<Error> errors = service.autoSaveSectionField(competitionResource, "fullApplicationFinance", "1", Optional.empty());

        assertTrue(errors.isEmpty());
        assertEquals(TRUE, competitionResource.isFullApplicationFinance());
        verify(competitionService).update(competitionResource);
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionErrors() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        competitionResource.setFullApplicationFinance(FALSE);

        List<Error> errors = service.autoSaveSectionField(competitionResource, "fullApplicationFinance", "asdfhkasdf", Optional.empty());

        assertTrue(errors.isEmpty());
        assertEquals(FALSE, competitionResource.isFullApplicationFinance());
        verify(competitionService).update(competitionResource);
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionUnknown() {
        CompetitionResource competition = newCompetitionResource().build();

        List<Error> errors = service.autoSaveSectionField(competition, "notExisting", "Strange!@#1Value", Optional.empty());

        assertTrue(!errors.isEmpty());
        verify(competitionService, never()).update(competition);
    }
}