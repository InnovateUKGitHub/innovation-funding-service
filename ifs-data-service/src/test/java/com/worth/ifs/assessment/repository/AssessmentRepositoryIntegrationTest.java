package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
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
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
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
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

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

        Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withProcessOutcome(asList(processOutcome1), asList(processOutcome2))
                .withParticipant(processRole1, processRole2)
                .withActivityState(openState)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toList());

        final Assessment found = repository.findOneByParticipantId(processRole1.getId());
        assertEquals(saved.get(0), found);
    }

    @Test
    @Rollback
    public void testFindByUserIdAndCompetitionId() throws Exception {
        Long userId = 3L;
        Long competitionId = 1L;

        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        User user = newUser()
                .withId(3L)
                .withFirstName("Professor")
                .build();

        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        Competition competition = competitionRepository.findById(competitionId) ;
        CompetitionInvite invite = new CompetitionInvite("Professor", "paul.plum@gmail.com", "hash", competition);
        competitionParticipantRepository.save( new CompetitionParticipant(competition, user, invite));
        List<ProcessRole> processRoles = processRoleRepository.findByUserId(userId);

        List<Assessment> assessments = newAssessment()
                .withApplication(application)
                .withParticipant(processRoles.get(0), processRoles.get(1),processRoles.get(2), processRoles.get(3))
                .withActivityState(openState)
                .build(4);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toList());
        List<Assessment> found = repository.findByParticipantUserIdAndParticipantApplicationCompetitionId(userId, competitionId);
        assertEquals(saved, found);
    }
}