package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseServiceUnitTest<AssessmentService> {

    @Mock
    private AssessmentRestService assessmentRestService;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private ProcessRoleService processRoleService;
    @Mock
    private CompetitionService competitionService;
    @Mock
    private QuestionService questionService;

    @Override
    protected AssessmentService supplyServiceUnderTest() {
        return new AssessmentServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        Long assessmentId = 1L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(expected));

        AssessmentResource response = service.getById(assessmentId);

        assertSame(expected, response);
        verify(assessmentRestService, only()).getById(assessmentId);
    }

    @Test
    public void testGetAllQuestionsById() throws ExecutionException, InterruptedException {
        Long assessmentId = 1L;
        Long competitionId = 2L;
        Long processRoleId = 3L;
        Long applicationId = 4L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(newAssessmentResource()
                .with(id(assessmentId))
                .withProcessRole(processRoleId)
                .build()));

        when(processRoleService.getById(processRoleId)).thenReturn(settable(newProcessRoleResource()
                .with(id(processRoleId))
                .withApplication(applicationId)
                .build()));

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource()
                .with(id(applicationId))
                .withCompetition(competitionId)
                .build());

        List<QuestionResource> expectedQuestions = newQuestionResource().build(1);
        when(questionService.findByCompetition(competitionId)).thenReturn(expectedQuestions);

        List<QuestionResource> response = service.getAllQuestionsById(assessmentId);
        assertSame(expectedQuestions, response);
    }
}