package com.worth.ifs.workflow.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProcessOutcomeServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    ProcessOutcomeServiceImpl processOutcomeService;

    @Mock
    ProcessOutcomeRepository processOutcomeRepository;

    @Test
    public void testGetById() {

        Long processOutcomeId = 1L;

        ProcessOutcome processOutcome = newProcessOutcome().with(id(processOutcomeId)).build();

        when(processOutcomeRepository.findOne(processOutcomeId)).thenReturn(processOutcome);
        final ProcessOutcome resultProcessOutcome = processOutcomeService.findOne(processOutcomeId);

        assertEquals(processOutcomeId,resultProcessOutcome.getId());
    }
}
