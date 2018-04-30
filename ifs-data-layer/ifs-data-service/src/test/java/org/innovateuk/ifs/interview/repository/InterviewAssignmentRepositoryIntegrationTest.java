package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterviewAssignmentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InterviewAssignmentRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    @Override
    protected void setRepository(InterviewAssignmentRepository repository) {
        this.repository = repository;
    }

    private final Set<State> NOTIFIED_INTERVIEW_ASSIGNMENT_STATES =
            simpleMapSet(
                    asList(AWAITING_FEEDBACK_RESPONSE, InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE),
                    InterviewAssignmentState::getBackingState
            );

    @Test
    public void findByTargetCompetitionIdAndActivityStateState() {
        Pageable pageable = new PageRequest(0, 20);

        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();

        applicationRepository.save(application);

        InterviewAssignment expected = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState(CREATED))
                .withTarget(application)
                .build();

        repository.save(expected);

        flushAndClearSession();

        Page<InterviewAssignment> actual = repository.findByTargetCompetitionIdAndActivityStateState(competition.getId(), activityState(CREATED).getState(), pageable);

        assertEquals(expected.getId(), actual.getContent().get(0).getId());
    }

    @Test
    public void existsByTargetIdAndActivityStateState() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();

        applicationRepository.save(application);

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                InterviewAssignmentState.CREATED.getBackingState()
        );

        InterviewAssignment expected = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState)
                .withTarget(application)
                .build();

        repository.save(expected);

        flushAndClearSession();

        boolean interviewAssignmentExists = repository.existsByTargetIdAndActivityStateStateIn(application.getId(), singletonList(activityState.getState()));

        assertTrue(interviewAssignmentExists);
    }

    @Test
    public void countByTargetCompetitionIdAndActivityStateState() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build(3);

        applicationRepository.save(applications);

        List<InterviewAssignment> assignments = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState(CREATED), activityState(AWAITING_FEEDBACK_RESPONSE), activityState(SUBMITTED_FEEDBACK_RESPONSE))
                .withTarget(applications.toArray(new Application[3]))
                .build(3);

        repository.save(assignments);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

        assertEquals(2, count);
    }

    @Test
    public void countByTargetCompetitionIdAndActivityStateState_createdAssignment() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();

        applicationRepository.save(application);

        InterviewAssignment assignment = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState(CREATED))
                .withTarget(application)
                .build();

        repository.save(assignment);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

        assertEquals(0, count);
    }

    @Test
    public void countByTargetCompetitionIdAndActivityStateState_noApplications() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

        assertEquals(0, count);
    }

    private ActivityState activityState(InterviewAssignmentState interviewAssignmentState) {
        return activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                interviewAssignmentState.getBackingState()
        );
    }
}