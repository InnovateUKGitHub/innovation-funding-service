package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.workflow.resource.ActivityType.APPLICATION_ASSESSMENT;
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
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

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

        final ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        final ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        final ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        final ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        final Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        final List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withParticipant(processRole1, processRole2)
                .withActivityState(openState)
                .build(2);

        final Set<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toSet());

        final Set<Assessment> found = repository.findAll();
        assertEquals(2, found.size());
        assertEquals(saved, found);
    }


    @Test
    @Rollback
    public void testFindOneByProcessRoleId() throws Exception {
        final ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        final ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .build());

        final ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        final ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        final Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        final List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withParticipant(processRole1, processRole2)
                .withActivityState(openState)
                .build(2);

        final List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toList());

        final Assessment found = repository.findOneByParticipantId(processRole1.getId());
        assertEquals(saved.get(0), found);
    }
}