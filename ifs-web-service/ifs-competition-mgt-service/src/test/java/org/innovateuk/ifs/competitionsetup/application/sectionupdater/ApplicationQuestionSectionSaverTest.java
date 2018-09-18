package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.form.QuestionForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
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
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder
        .newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    public void doSaveSectionShouldTriggerIrregularSaveActionAndAppendFileTypes() {
        List<FileTypeCategory> fileTypeCategories = asList(PDF, SPREADSHEET);
        Set<String> fileTypeCategoriesStringList = simpleMapSet(fileTypeCategories, FileTypeCategory::name);
        String fileTypeCategoriesString = String.join(",", fileTypeCategoriesStringList);

        QuestionForm competitionSetupForm = new QuestionForm();
        CompetitionSetupQuestionResource question = new CompetitionSetupQuestionResource();
        question.setQuestionId(1L);
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(questionSetupCompetitionRestService.getByQuestionId(1L)).thenReturn(restSuccess(question));
        when(questionSetupCompetitionRestService.save(question)).thenReturn(restSuccess());

        service.autoSaveSectionField(competition,
                competitionSetupForm,
                "allowedFileTypes",
                fileTypeCategoriesString,
                Optional.of(1L));

        ArgumentCaptor<CompetitionSetupQuestionResource> captor = ArgumentCaptor.forClass(CompetitionSetupQuestionResource.class);
        verify(questionSetupCompetitionRestService).save(captor.capture());

        assertThat(captor.getValue().getAllowedFileTypes()).containsExactlyInAnyOrderElementsOf(fileTypeCategories);
    }

    @Test
    public void doSaveSectionShouldTriggerIrregularSaveActionAndUpdateWithEmptyFileTypes() {
        QuestionForm competitionSetupForm = new QuestionForm();
        CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource()
                .withQuestionId(1L)
                .build();
        competitionSetupForm.setQuestion(question);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();

        when(questionSetupCompetitionRestService.getByQuestionId(1L)).thenReturn(restSuccess(question));
        when(questionSetupCompetitionRestService.save(question)).thenReturn(restSuccess());

        service.autoSaveSectionField(competition,
                competitionSetupForm,
                "allowedFileTypes",
                "",
                Optional.of(1L)
        );

        ArgumentCaptor<CompetitionSetupQuestionResource> captor = ArgumentCaptor.forClass(CompetitionSetupQuestionResource.class);
        verify(questionSetupCompetitionRestService).save(captor.capture());

        assertThat(captor.getValue().getAllowedFileTypes()).isEmpty();
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(QuestionForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

}
