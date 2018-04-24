package org.innovateuk.ifs.competitionsetup.common.service;

import org.innovateuk.ifs.commons.service.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.common.service.CompetitionSetupQuestionServiceImpl;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupQuestionServiceImplTest {
    @InjectMocks
	private CompetitionSetupQuestionServiceImpl service;

	@Mock
	private QuestionSetupCompetitionRestService restService;

    @Test
    public void testGetQuestion() {
        long questionId = 1L;
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();

        when(restService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));

        ServiceResult<CompetitionSetupQuestionResource> result = service.getQuestion(questionId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), resource);
    }

    @Test
    public void testSave() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();

        when(restService.save(resource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateQuestion(resource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testCreateDefaultQuestion() throws Exception {
        long competitionId = 1L;
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();

        when(restService.addDefaultToCompetition(competitionId)).thenReturn(restSuccess(resource));

        ServiceResult<CompetitionSetupQuestionResource> result = service.createDefaultQuestion(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), resource);
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        final Long questionId = 1L;

        when(restService.deleteById(questionId)).thenReturn(restSuccess());
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestion(questionId);
        assertTrue(resultAssessedQuestion.isSuccess());
    }
}
