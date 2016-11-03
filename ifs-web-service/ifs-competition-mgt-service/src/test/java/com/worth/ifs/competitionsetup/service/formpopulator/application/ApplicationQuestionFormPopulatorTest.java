package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionFormPopulatorTest {

    @InjectMocks
	private ApplicationQuestionFormPopulator populator;

    @Mock
    private QuestionService questionService;

    @Mock
    private FormInputService formInputService;

    private Long questionId = 7890L;
    private Long questionNotFoundId = 12904L;
    private Long competitionId = 2345L;
    private String questionTitle = "My question";
    private String guidanceTitle = "My guidance";
    private QuestionResource questionResource;
    private CompetitionResource competitionResource;

    @Before
	public void setUp() {
        List<FormInputResource> formInputs = newFormInputResource()
                .withQuestion(questionId)
                .withFormInputType(1L)
                .with(formInputResource -> {
                    formInputResource.setGuidanceQuestion(guidanceTitle);
                })
                .build(1);
        List<FormInputResource> formAssessmentInputs = Collections.emptyList();

        when(formInputService.findApplicationInputsByQuestion(questionId)).thenReturn(formInputs);
        when(formInputService.findAssessmentInputsByQuestion(questionId)).thenReturn(formAssessmentInputs);

        questionResource = newQuestionResource()
                .withId(questionId)
                .withName(questionTitle)
                .build();
        competitionResource = newCompetitionResource().withId(competitionId).build();

        when(questionService.getById(questionNotFoundId)).thenThrow(new ObjectNotFoundException());
        when(questionService.getById(questionId)).thenReturn(questionResource);

	}

	@Test
    public void testPopulateFormWithoutErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result instanceof ApplicationQuestionForm);
        ApplicationQuestionForm form = (ApplicationQuestionForm) result;
        assertEquals(questionId, form.getQuestion().getId());
        assertEquals(questionTitle, form.getQuestion().getTitle());
        assertEquals(guidanceTitle, form.getQuestion().getGuidanceTitle());
        assertEquals(Boolean.FALSE, form.getQuestion().getAppendix());
        assertEquals(Boolean.FALSE, form.getQuestion().getScored());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionNotFoundId));
        assertEquals(null, result);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithNoObjectIdErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.empty());
        assertEquals(null, result);
    }

    @Test
    public void testPopulateFormWithNoFormInput() {
        when(formInputService.findApplicationInputsByQuestion(questionId)).thenReturn(Collections.emptyList());
        when(formInputService.findAssessmentInputsByQuestion(questionId)).thenReturn(Collections.emptyList());

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result instanceof ApplicationQuestionForm);
        ApplicationQuestionForm form = (ApplicationQuestionForm) result;
        assertEquals(null, form.getQuestion());
    }
}
