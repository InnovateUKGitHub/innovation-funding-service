package com.worth.ifs.application.service;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;

@RunWith(MockitoJUnitRunner.class)
public class AssessorFeedbackServiceImplTest {
	
	@InjectMocks
	private AssessorFeedbackServiceImpl service;
	
	@Mock
	private AssessorFeedbackRestService assessorFeedbackRestService;
	
	@Test
	public void testSubmitAssessorFeedback() {
		Long competitionId = 123L;
		
		when(assessorFeedbackRestService.submitAssessorFeedback(competitionId)).thenReturn(restSuccess());

		service.submitAssessorFeedback(competitionId);
		
		verify(assessorFeedbackRestService).submitAssessorFeedback(competitionId);
	}
	
	@Test(expected = GeneralUnexpectedErrorException.class)
	public void testErrorSubmittingAssessorFeedback() {
		Long competitionId = 123L;
		
		when(assessorFeedbackRestService.submitAssessorFeedback(competitionId)).thenReturn(restFailure(CommonErrors.internalServerErrorError()));
		service.submitAssessorFeedback(competitionId);
	}
	
	@Test
	public void testFeedbackUploaded() {
		Long competitionId = 123L;
		
		when(assessorFeedbackRestService.feedbackUploaded(competitionId)).thenReturn(restSuccess(true));

		service.feedbackUploaded(competitionId);
		
		verify(assessorFeedbackRestService).feedbackUploaded(competitionId);
	}
	
	@Test(expected = GeneralUnexpectedErrorException.class)
	public void testErrorFeedbackUploaded() {
		Long competitionId = 123L;
		
		when(assessorFeedbackRestService.feedbackUploaded(competitionId)).thenReturn(restFailure(CommonErrors.internalServerErrorError()));
		service.feedbackUploaded(competitionId);
	}

}
