package com.worth.ifs.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competitionsetup.model.Question;
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
		Question question = new Question();
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

}
