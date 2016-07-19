package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.builder.ProcessRoleBuilder;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    @Test
    public void findById() throws Exception {
        final Long assessmentId  = 1L;

        final Assessment assessment = newAssessment()
                        .build();

        final AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        final AssessmentResource found = assessmentService.findById(assessmentId).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentRepositoryMock, only()).findOne(assessmentId);
    }

    @Test
    public void rejectApplication() throws Exception {

        final Long processRoleId = 1L;
        final Long assessmentId  = 1L;

        final ProcessRole processRole = ProcessRoleBuilder.newProcessRole().withId(processRoleId).build();
        final Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
        final ServiceResult<Void> result = assessmentService.updateStatus(assessmentId, processOutcome);
        assertTrue(result.isSuccess());
    }
}