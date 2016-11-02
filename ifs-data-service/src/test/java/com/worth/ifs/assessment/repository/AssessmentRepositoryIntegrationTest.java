package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
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
    private UserRepository userRepository;

    @Autowired
    @Override
    protected void setRepository(final AssessmentRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .withIndex(0)
                .build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .withIndex(0)
                .build());

        ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withParticipant(processRole1, processRole2)
                .withActivityState(openState)
                .build(2);

        Set<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toSet());

        Set<Assessment> found = repository.findAll();
        assertEquals(2, found.size());
        assertEquals(saved, found);
    }

    @Test
    public void findOneByParticipantId() throws Exception {
        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome()
                .withIndex(0)
                .build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome()
                .withIndex(0)
                .build());

        ProcessRole processRole1 = processRoleRepository.save(newProcessRole()
                .build());

        ProcessRole processRole2 = processRoleRepository.save(newProcessRole()
                .build());

        Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withParticipant(processRole1, processRole2)
                .withActivityState(openState)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(toList());

        final Assessment found = repository.findOneByParticipantId(processRole1.getId());
        assertEquals(saved.get(0), found);
    }

    @Test
    public void findByParticipantUserIdAndParticipantApplicationCompetitionId() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        Long userId = 3L;

        User user = userRepository.findOne(userId);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<ProcessRole> applicationParticipants = newProcessRole()
                .withId((Long) null)
                .withUser(user)
                .withRole(ASSESSOR)
                .withApplication(application)
                .build(2).stream().map(processRole -> processRoleRepository.save(processRole)).collect(toList());

        List<Assessment> assessments = newAssessment()
                .withId((Long) null)
                .withApplication(application)
                .withParticipant(applicationParticipants.get(0), applicationParticipants.get(1))
                .withActivityState(openState)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(toList());
        List<Assessment> found = repository.findByParticipantUserIdAndParticipantApplicationCompetitionId(userId, application.getCompetition().getId());
        assertEquals(saved, found);
    }
}