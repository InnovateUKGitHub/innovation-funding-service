package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionServiceImpl;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupQuestionServiceImplTest {

	@InjectMocks
	private CompetitionSetupQuestionServiceImpl service;
	
	@Mock
	private CompetitionService competitionService;

	@Mock
	private FormInputService formInputService;

	@Mock
	private QuestionService questionService;

	@Test
	public void testUpdateQuestion() {
		QuestionViewModel question = new QuestionViewModel();
		question.setId(2L);
        question.setAppendix(false);
        question.setScored(false);
		question.setGuidance("Guidance");
        question.setTitle("Title");
		question.setShortTitle("ShortTitle");
		question.setGuidanceTitle("Guidance Title");
		question.setMaxWords(123);

        when(questionService.getById(question.getId())).thenReturn(newQuestionResource().build());
        when(formInputService.findApplicationInputsByQuestion(question.getId())).thenReturn(asList());
        when(formInputService.findAssessmentInputsByQuestion(question.getId())).thenReturn(asList());

		service.updateQuestion(question);
		
		verify(formInputService).save(any(FormInputResource.class));
		verify(questionService).save(any(QuestionResource.class));
	}

	@Test
	public void testGetQuestion() {
        Long questionId = 1234L;

        FormInputResource formInput = new FormInputResource();
        formInput.setFormInputType(2L);
        formInput.setGuidanceQuestion("Guidance");
        formInput.setGuidanceAnswer("Answer");
        formInput.setWordCount(500);

        when(questionService.getById(questionId)).thenReturn(
                newQuestionResource()
                        .withId(questionId)
                        .withName("Title")
                        .withDescription("ShortTitle")
                        .build());
		when(formInputService.findApplicationInputsByQuestion(questionId)).thenReturn(asList(formInput));
		when(formInputService.findAssessmentInputsByQuestion(questionId)).thenReturn(asList());

		QuestionViewModel questionResult = service.getQuestion(questionId);

		assertEquals(questionId, questionResult.getId());
        assertEquals("Title", questionResult.getTitle());
        assertEquals("ShortTitle", questionResult.getSubTitle());
        assertEquals("Guidance", questionResult.getGuidanceTitle());
        assertEquals("Answer", questionResult.getGuidance());
        assertEquals(Integer.valueOf(500), questionResult.getMaxWords());
	}
}
