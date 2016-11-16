package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.*;
import com.worth.ifs.commons.service.*;
import com.worth.ifs.competition.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.commons.rest.RestResult.*;
import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.*;
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
        CompetitionSetupQuestionResource resource = newCompetitionSetupQuestionResource().build();

        when(restService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));

        ServiceResult<CompetitionSetupQuestionResource> result = service.getQuestion(questionId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObjectOrThrowException(), resource);
    }

    @Test
    public void testSave() {
        CompetitionSetupQuestionResource resource = newCompetitionSetupQuestionResource().build();

        when(restService.save(resource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateQuestion(resource);

        assertTrue(result.isSuccess());
    }
}
