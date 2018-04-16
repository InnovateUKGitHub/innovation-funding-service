package org.innovateuk.ifs.assessment.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
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

        Page<InterviewAssignment> actual = repository.findByTargetCompetitionIdAndActivityStateState(competition.getId(), activityState.getState(), pageable);

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
}