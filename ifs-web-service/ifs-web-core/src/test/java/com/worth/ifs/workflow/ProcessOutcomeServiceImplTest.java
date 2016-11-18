package com.worth.ifs.workflow;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.service.ProcessOutcomeRestService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class ProcessOutcomeServiceImplTest extends BaseServiceUnitTest<ProcessOutcomeService> {

    @Mock
    private ProcessOutcomeRestService processOutcomeRestService;

    @Override
    protected ProcessOutcomeService supplyServiceUnderTest() {
        return new ProcessOutcomeServiceImpl();
    }

    @Test
    public void getById() throws Exception {
        Long processOutcomeId = 1L;

        ProcessOutcomeResource expected = newProcessOutcomeResource().build();

        when(processOutcomeRestService.findOne(processOutcomeId)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getById(processOutcomeId));
        verify(processOutcomeRestService, only()).findOne(processOutcomeId);
    }

    @Test
    public void getByProcessId() throws Exception {
        Long processId = 1L;

        ProcessOutcomeResource expected = newProcessOutcomeResource().build();

        when(processOutcomeRestService.findLatestByProcessId(processId)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getByProcessId(processId));
        verify(processOutcomeRestService, only()).findLatestByProcessId(processId);
    }

    @Test
    public void getByProcessIdAndOutcomeType() throws Exception {
        Long processId = 1L;
        String outcomeType = AssessmentOutcomes.SUBMIT.getType();

        ProcessOutcomeResource expected = newProcessOutcomeResource().build();

        when(processOutcomeRestService.findLatestByProcessIdAndType(processId, outcomeType)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getByProcessIdAndOutcomeType(processId, outcomeType));
        verify(processOutcomeRestService, only()).findLatestByProcessIdAndType(processId, outcomeType);
    }
}