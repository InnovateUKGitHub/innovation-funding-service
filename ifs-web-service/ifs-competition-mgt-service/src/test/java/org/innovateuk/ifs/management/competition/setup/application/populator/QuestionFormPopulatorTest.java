package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuestionFormPopulatorTest {

    @InjectMocks
    private QuestionFormPopulator populator;

    @Mock
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private SectionService sectionService;

    private Long questionId = 7890L;
    private Long questionNotFoundId = 12904L;
    private QuestionResource questionResource = newQuestionResource().withId(questionId).build();
    private CompetitionResource competitionResource;


    @Test
    public void populateForm_withoutErrors() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionResource = newSectionResource().withQuestions(Arrays.asList(1L, 2L)).build();

        when(questionRestService.findById(questionId)).thenReturn(restSuccess(questionResource));
        when(questionSetupCompetitionRestService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionResource);

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result instanceof QuestionForm);
        QuestionForm form = (QuestionForm) result;
        assertEquals(form.getQuestion(), resource);
    }

    @Test
    public void populateForm_questionShouldNotBeRemovableIfLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithOneQuestion = newSectionResource().withQuestions(Arrays.asList(1L)).build();

        when(questionRestService.findById(questionId)).thenReturn(restSuccess(questionResource));
        when(questionSetupCompetitionRestService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithOneQuestion);

        QuestionForm result = (QuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

        assertFalse(result.isRemovable());
    }

    @Test
    public void populateForm_questionShouldBeRemovableIfNotLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithMultipleQuestions = newSectionResource().withQuestions(Arrays.asList(1L, 2L)).build();

        when(questionRestService.findById(questionId)).thenReturn(restSuccess(questionResource));
        when(questionSetupCompetitionRestService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithMultipleQuestions);

        QuestionForm result = (QuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result.isRemovable());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void populateForm_withErrors() {
        when(questionSetupCompetitionRestService.getByQuestionId(questionNotFoundId)).thenThrow(
                new ObjectNotFoundException());
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionNotFoundId));
        assertEquals(null, result);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void populateForm_formWithNoObjectIdErrors() {
        assertNull(populator.populateForm(competitionResource, Optional.empty()));
    }

}
