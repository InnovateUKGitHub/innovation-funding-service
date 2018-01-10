package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionFormPopulatorTest {

    @InjectMocks
	private ApplicationQuestionFormPopulator populator;

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

        assertTrue(result instanceof ApplicationQuestionForm);
        ApplicationQuestionForm form = (ApplicationQuestionForm) result;
        assertEquals(form.getQuestion(), resource);
    }

    @Test
    public void testPopulate_questionShouldNotBeRemovableIfLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithOneQuestion = newSectionResource().withQuestions(Arrays.asList(1L)).build();

        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithOneQuestion);

        ApplicationQuestionForm result = (ApplicationQuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

        assertFalse(result.isRemovable());
    }

    @Test
    public void testPopulate_questionShouldBeRemovableIfNotLastInSection() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        SectionResource sectionWithMultipleQuestions = newSectionResource().withQuestions(Arrays.asList(1L, 2L)).build();

        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));
        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(sectionWithMultipleQuestions);

        ApplicationQuestionForm result = (ApplicationQuestionForm) populator.populateForm(competitionResource, Optional.of(questionId));

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
