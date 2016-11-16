package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.error.exception.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.*;
import com.worth.ifs.competitionsetup.form.application.*;
import com.worth.ifs.competitionsetup.service.*;
import com.worth.ifs.form.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.*;
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
        CompetitionSetupQuestionResource resource = newCompetitionSetupQuestionResource().build();
        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(competitionSetupQuestionService.getQuestion(questionId)).thenReturn(serviceSuccess(resource));

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));


        assertTrue(result instanceof ApplicationQuestionForm);
        ApplicationQuestionForm form = (ApplicationQuestionForm) result;
        assertEquals(form.getQuestion(), resource);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithErrors() {
        when(questionService.getById(questionNotFoundId)).thenThrow(new ObjectNotFoundException());
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionNotFoundId));
        assertEquals(null, result);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithNoObjectIdErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.empty());
        assertEquals(null, result);
    }

}
