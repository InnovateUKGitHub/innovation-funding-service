package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.*;
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
	private CompetitionSetupQuestionRestService restService;

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
        final Long assessedQuestionId = 1L;
        final Long projectDetailsQuestionId = 2L;
        final String sectionNameAssessedQuestion = "Application questions";
        final String sectionNameProjectDetails = "Project details";

        when(restService.deleteByIdAndSection(assessedQuestionId, sectionNameAssessedQuestion)).thenReturn(restSuccess());
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestionForSection(assessedQuestionId, sectionNameAssessedQuestion);
        assertTrue(resultAssessedQuestion.isSuccess());

        when(restService.deleteByIdAndSection(projectDetailsQuestionId, sectionNameProjectDetails)).thenReturn(restSuccess());
        ServiceResult<Void> resultProjectDetails = service.deleteQuestionForSection(projectDetailsQuestionId, sectionNameProjectDetails);
        assertTrue(resultProjectDetails.isSuccess());
    }
}
