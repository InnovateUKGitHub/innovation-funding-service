package com.worth.ifs.workflow.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProcessOutcomeServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    ProcessOutcomeServiceImpl processOutcomeService;

    @Mock
    ProcessOutcomeRepository processOutcomeRepository;

    @Test
    public void getById() {
        Long processOutcomeId = 1L;

        ProcessOutcome processOutcome = newProcessOutcome().with(id(processOutcomeId)).build();
        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource().build();

        when(processOutcomeRepository.findOne(processOutcomeId)).thenReturn(processOutcome);
        when(processOutcomeMapperMock.mapToResource(processOutcome)).thenReturn(processOutcomeResource);

        final ServiceResult<ProcessOutcomeResource> result = processOutcomeService.findOne(processOutcomeId);

        assertTrue(result.isSuccess());
        assertEquals(processOutcomeResource, result.getSuccessObject());
    }

    @Test
    public void getByProcessId() {
        Long processId = 1L;

        ProcessOutcome processOutcome = newProcessOutcome().build();
        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource().build();

        when(processOutcomeRepository.findTopByProcessIdOrderByIdAsc(processId)).thenReturn(processOutcome);
        when(processOutcomeMapperMock.mapToResource(processOutcome)).thenReturn(processOutcomeResource);

        final ServiceResult<ProcessOutcomeResource> result = processOutcomeService.findLatestByProcess(processId);

        assertTrue(result.isSuccess());
        assertEquals(processOutcomeResource, result.getSuccessObject());
    }

    @Test
    public void getByProcessIdAndType() {
        Long processId = 1L;
        String outcomeType = AssessmentOutcomes.ACCEPT.getType();

        ProcessOutcome processOutcome = newProcessOutcome().build();
        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource().build();

        when(processOutcomeRepository.findTopByProcessIdAndOutcomeTypeOrderByIdAsc(processId, outcomeType)).thenReturn(processOutcome);
        when(processOutcomeMapperMock.mapToResource(processOutcome)).thenReturn(processOutcomeResource);

        final ServiceResult<ProcessOutcomeResource> result = processOutcomeService.findLatestByProcessAndOutcomeType(processId, outcomeType);

        assertTrue(result.isSuccess());
        assertEquals(processOutcomeResource, result.getSuccessObject());
    }
}
