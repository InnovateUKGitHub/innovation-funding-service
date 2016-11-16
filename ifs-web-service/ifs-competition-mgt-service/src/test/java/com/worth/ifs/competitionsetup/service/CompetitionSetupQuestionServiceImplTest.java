package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.service.FormInputService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
		question.setSubTitle("SubTitle");
		question.setGuidanceTitle("Guidance Title");
		question.setMaxWords(123);

        when(questionService.getById(question.getId())).thenReturn(newQuestionResource().build());
        when(formInputService.findApplicationInputsByQuestion(question.getId())).thenReturn(asList());
        when(formInputService.findAssessmentInputsByQuestion(question.getId())).thenReturn(asList());

		service.updateQuestion(question);

		ArgumentCaptor<QuestionResource> questionResourceArgumentCaptor = ArgumentCaptor.forClass(QuestionResource.class);
		verify(questionService).save(questionResourceArgumentCaptor.capture());

		assertEquals(question.getTitle(), questionResourceArgumentCaptor.getValue().getName());
		assertEquals(question.getShortTitle(), questionResourceArgumentCaptor.getValue().getShortName());
		assertEquals(question.getSubTitle(), questionResourceArgumentCaptor.getValue().getDescription());

		ArgumentCaptor<FormInputResource> formInputResourceArgumentCaptor = ArgumentCaptor.forClass(FormInputResource.class);
		verify(formInputService).save(formInputResourceArgumentCaptor.capture());

		Long textAreaInputTypeId = 2L;

		assertEquals(formInputResourceArgumentCaptor.getValue().getQuestion(), question.getId());
		assertEquals(formInputResourceArgumentCaptor.getValue().getScope(), FormInputScope.APPLICATION);
		assertEquals(formInputResourceArgumentCaptor.getValue().getFormInputType(), textAreaInputTypeId);
		assertEquals(formInputResourceArgumentCaptor.getValue().getGuidanceQuestion(), question.getGuidanceTitle());
		assertEquals(formInputResourceArgumentCaptor.getValue().getGuidanceAnswer(), question.getGuidance());
		assertEquals(formInputResourceArgumentCaptor.getValue().getWordCount(), question.getMaxWords());
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
