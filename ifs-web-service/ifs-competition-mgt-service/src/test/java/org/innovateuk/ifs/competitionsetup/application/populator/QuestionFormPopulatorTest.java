package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competitionsetup.application.form.QuestionForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionFormPopulatorTest {

    @InjectMocks
	private QuestionFormPopulator populator;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private SectionService sectionService;

    private Long questionId = 7890L;
    private Long questionNotFoundId = 12904L;
    private QuestionResource questionResource = newQuestionResource().withId(questionId).build();
    private CompetitionResource competitionResource;


	@Test
    public void testPopulateFormWithoutErrors() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionResource = newSectionResource().withQuestions(Arrays.asList(1L, 2L)).build();

        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionResource);

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result instanceof QuestionForm);
        QuestionForm form = (QuestionForm) result;
        assertEquals(form.getQuestion(), resource);
    }

    @Test
    public void testPopulate_questionShouldNotBeRemovableIfLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithOneQuestion = newSectionResource().withQuestions(Arrays.asList(1L)).build();

        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithOneQuestion);

        QuestionForm result = (QuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

        assertFalse(result.isRemovable());
    }

    @Test
    public void testPopulate_questionShouldBeRemovableIfNotLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithMultipleQuestions = newSectionResource().withQuestions(Arrays.asList(1L, 2L)).build();

        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithMultipleQuestions);

        QuestionForm result = (QuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result.isRemovable());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithErrors() {
        when(competitionSetupQuestionService.getQuestion(questionNotFoundId)).thenThrow(new ObjectNotFoundException());
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionNotFoundId));
        assertEquals(null, result);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithNoObjectIdErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.empty());
        assertEquals(null, result);
    }

}
