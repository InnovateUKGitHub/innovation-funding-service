package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.form.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionSectionSaverTest {

    @InjectMocks
    private ApplicationQuestionSectionUpdater service;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.QUESTIONS, service.subsectionToSave());
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
    public void test_doSaveSectionShouldTriggerIrregularSaveActionAndAppendFileTypes() {
        List<FileTypeCategory> fileTypeCategories = asList(FileTypeCategory.PDF, FileTypeCategory.SPREADSHEET);
        Set<String> fileTypeCategoriesStringList = simpleMapSet(fileTypeCategories, FileTypeCategory::name);
        String fileTypeCategoriesString = String.join(",", fileTypeCategoriesStringList);

        ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(1L);
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));
        when(competitionSetupQuestionService.updateQuestion(question)).thenReturn(serviceSuccess());

        service.autoSaveSectionField(competition,
                competitionSetupForm,
                "allowedFileTypes",
                fileTypeCategoriesString,
                Optional.of(1L));

        ArgumentCaptor<CompetitionSetupQuestionResource> captor = ArgumentCaptor.forClass(CompetitionSetupQuestionResource.class);
        verify(competitionSetupQuestionService).updateQuestion(captor.capture());

        assertThat(captor.getValue().getAllowedFileTypes()).containsExactlyInAnyOrderElementsOf(fileTypeCategories);
    }

    @Test
    public void test_doSaveSectionShouldTriggerIrregularSaveActionAndUpdateWithEmptyFileTypes() {
        ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(1L);
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(competitionSetupQuestionService.getQuestion(1L)).thenReturn(serviceSuccess(question));
        when(competitionSetupQuestionService.updateQuestion(question)).thenReturn(serviceSuccess());

        service.autoSaveSectionField(competition,
                competitionSetupForm,
                "allowedFileTypes",
                "",
                Optional.of(1L)
        );

        ArgumentCaptor<CompetitionSetupQuestionResource> captor = ArgumentCaptor.forClass(CompetitionSetupQuestionResource.class);
        verify(competitionSetupQuestionService).updateQuestion(captor.capture());

        assertThat(captor.getValue().getAllowedFileTypes()).isEmpty();
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(ApplicationQuestionForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

}
