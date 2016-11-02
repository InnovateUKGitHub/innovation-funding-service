package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.junit.Assert.assertEquals;

public class ProcessOutcomeRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProcessOutcomeRepository> {

    @Autowired
    ProcessOutcomeRepository processOutcomeRepository;

    @Autowired
    @Override
    protected void setRepository(final ProcessOutcomeRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testFindAll() throws Exception {
        repository.deleteAll();

        List<ProcessOutcome> processOutcomeList = newProcessOutcome()
                .withIndex(0,1,2)
                .build(3);
        final List<ProcessOutcome> saved = processOutcomeList.stream().map(processOutcome -> repository.save(processOutcome)).collect(Collectors.toList());
        final List<ProcessOutcome> found = (List<ProcessOutcome>) repository.findAll();

        assertEquals(3, found.size());
        assertEquals(saved, found);
    }

    @Test
    public void testFindOne() throws Exception {
        repository.deleteAll();

        ProcessOutcome processOutcome = newProcessOutcome()
                .withIndex(0)
                .build();
        processOutcome.setOutcome(AssessmentOutcomes.ACCEPT.getType());
        final ProcessOutcome saved = repository.save(processOutcome);
        final ProcessOutcome found = repository.findOne(saved.getId());

        assertEquals(processOutcome.getOutcome(), found.getOutcome());
    }


}
