package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.commons.error.exception.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.form.*;
import org.innovateuk.ifs.competitionsetup.form.application.*;
import org.innovateuk.ifs.competitionsetup.service.*;
import org.innovateuk.ifs.form.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.*;
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
    private FormInputService formInputService;

    private Long questionId = 7890L;
    private Long questionNotFoundId = 12904L;
    private QuestionResource questionResource = newQuestionResource().withId(questionId).build();
    private CompetitionResource competitionResource;


	@Test
    public void testPopulateFormWithoutErrors() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));


        assertTrue(result instanceof ApplicationQuestionForm);
        ApplicationQuestionForm form = (ApplicationQuestionForm) result;
        assertEquals(form.getQuestion(), resource);
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
