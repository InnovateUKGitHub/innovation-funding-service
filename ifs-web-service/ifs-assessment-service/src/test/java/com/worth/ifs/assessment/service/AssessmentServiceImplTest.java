package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.builder.AssessmentResourceBuilder;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.builder.ProcessRoleResourceBuilder;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseServiceUnitTest<AssessmentService> {

    @Mock
    private AssessmentRestService assessmentRestService;
    @Mock
    private AssessmentService assessmentService;
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
        final AssessmentResource expected = newAssessmentResource()
                .build();

        final Long assessmentId = 1L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(expected));

        final AssessmentResource response = service.getById(assessmentId);

        assertSame(expected, response);
        verify(assessmentRestService, only()).getById(assessmentId);
    }
    @Test
    public void testGetAllQuestionsById() throws ExecutionException, InterruptedException {
        final Long assessmentId = 1L;
        final AssessmentResource assessmentResource = AssessmentResourceBuilder.newAssessmentResource().build();
        final ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource().build();
        final ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();
        final CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();
        final QuestionResource questionResource = QuestionResourceBuilder.newQuestionResource().build();

        List<QuestionResource> expected = new ArrayList<>();
        expected.add(questionResource);

        when(assessmentService.getById(assessmentId)).thenReturn(assessmentResource);
        when(processRoleService.getById(assessmentResource.getProcessRole())).thenReturn(settable(processRoleResource));
        when(applicationService.getById(processRoleResource.getApplication())).thenReturn(applicationResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        when(questionService.findByCompetition(competitionResource.getId())).thenReturn(expected);

        final List<QuestionResource> response = service.getAllQuestionsById(assessmentId);
        assertSame(expected, response);

    }
}