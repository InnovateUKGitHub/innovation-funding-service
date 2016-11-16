package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertArrayEquals;
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
    private RoleRepository roleRepository;

    @Autowired
    @Override
    protected void setRepository(final AssessmentRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome().build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome().build());

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
        ProcessOutcome processOutcome1 = processOutcomeRepository.save(newProcessOutcome().build());

        ProcessOutcome processOutcome2 = processOutcomeRepository.save(newProcessOutcome().build());

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
    public void findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        Long userId = 3L;

        Application application = applicationRepository.findOne(1L);
        int numOfAssessmentsForEachState = 2;

        setUpShuffledAssessments(userRepository.findOne(userId), application, numOfAssessmentsForEachState);

        List<Assessment> found = repository.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId, application.getCompetition().getId());

        assertEquals(AssessmentStates.values().length * numOfAssessmentsForEachState, found.size());

        Map<AssessmentStates, List<Assessment>> foundByStateMap = found.stream().collect(Collectors.groupingBy(Assessment::getActivityState, LinkedHashMap::new, toList()));

        assertArrayEquals("Expected the assessments to ordered by ActivityState in the natural ordering of their equivalent AssessmentStates", AssessmentStates.values(), foundByStateMap.keySet().toArray(new AssessmentStates[foundByStateMap.keySet().size()]));

        foundByStateMap.values().forEach(foundByState -> {
            List<Long> ids = getAssessmentIds(foundByState);
            assertEquals("Expected the assessments to be ordered by id after ordering by ActivityState", ids.stream().sorted().collect(toList()), ids);
        });
    }

    private void setUpShuffledAssessments(User user, Application application, int numOfAssessmentsForEachState) {
        List<ActivityState> states = getActivityStates();
        List<Assessment> assessments = states.stream().flatMap(activityState -> newAssessment()
                .withId((Long) null)
                .withApplication(application)
                .withParticipant(setUpParticipants(user, application, numOfAssessmentsForEachState))
                .withActivityState(activityState)
                .build(numOfAssessmentsForEachState).stream()).collect(toList());

        Collections.shuffle(assessments);
        repository.save(assessments);
    }

    private ProcessRole[] setUpParticipants(User user, Application application, int count) {
        List<ProcessRole> result = new ArrayList<>();
        processRoleRepository.save(newProcessRole()
                .withId((Long) null)
                .withUser(user)
                .withRole(roleRepository.findOneByName(ASSESSOR.getName()))
                .withApplication(application)
                .build(count)).forEach(result::add);
        return result.toArray(new ProcessRole[result.size()]);
    }

    private List<Long> getAssessmentIds(List<Assessment> assessments) {
        return assessments.stream().map(Process::getId).collect(toList());
    }

    private List<ActivityState> getActivityStates() {
        return Arrays.stream(AssessmentStates.values()).map(assessmentState -> activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, assessmentState.getBackingState())).collect(toList());
    }
}