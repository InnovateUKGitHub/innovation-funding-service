package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AssessmentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentRepository> {

    @Autowired
    private ProcessOutcomeRepository processOutcomeRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    @Override
    protected void setRepository(final AssessmentRepository repository) {
        this.repository = repository;
    }

    @Test
    @Rollback
    public void testFindAll() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        List<Assessment> assessments = newAssessment()
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withProcessRole(processRole1, processRole2)
                .build(2);

        Set<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toSet());

        Set<Assessment> found = repository.findAll();
        assertEquals(2, found.size());
        assertEquals(saved, found);
    }

    @Test
    @Rollback
    public void testFindOneByProcessRoleId() throws Exception {
        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        List<Assessment> assessments = newAssessment()
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withProcessRole(processRole1, processRole2)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toList());

        Assessment found = repository.findOneByProcessRoleId(processRole1.getId());
        assertEquals(saved.get(0), found);
    }

    @Test
    @Rollback
    public void testFindByProcessRoles() throws Exception {
        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        List<Assessment> assessments = newAssessment()
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withProcessRole(processRole1, processRole2)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toList());

        List<Assessment> found = repository.findByProcessRoleIn(asList(processRole1, processRole2));
        assertEquals(saved, found);
    }
}