package org.innovateuk.ifs.workflow.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.repository.ProcessOutcomeRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.innovateuk.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
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

        when(processOutcomeRepository.findTopByProcessIdOrderByIdDesc(processId)).thenReturn(processOutcome);
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

        when(processOutcomeRepository.findTopByProcessIdAndOutcomeTypeOrderByIdDesc(processId, outcomeType)).thenReturn(processOutcome);
        when(processOutcomeMapperMock.mapToResource(processOutcome)).thenReturn(processOutcomeResource);

        final ServiceResult<ProcessOutcomeResource> result = processOutcomeService.findLatestByProcessAndOutcomeType(processId, outcomeType);

        assertTrue(result.isSuccess());
        assertEquals(processOutcomeResource, result.getSuccessObject());
    }
}
