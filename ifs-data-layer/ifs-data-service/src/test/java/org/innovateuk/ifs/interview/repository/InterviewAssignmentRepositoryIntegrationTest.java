package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterviewAssignmentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InterviewAssignmentRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    @Override
    protected void setRepository(InterviewAssignmentRepository repository) {
        this.repository = repository;
    }

    private final Set<InterviewAssignmentState> NOTIFIED_INTERVIEW_ASSIGNMENT_STATES =
            asLinkedSet(AWAITING_FEEDBACK_RESPONSE, InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE);

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
                .withState(CREATED)
                .withTarget(application)
                .build();

        repository.save(expected);

        flushAndClearSession();

        Page<InterviewAssignment> actual = repository.findByTargetCompetitionIdAndActivityState(competition.getId(), CREATED, pageable);

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

        InterviewAssignment expected = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(application)
                .build();

        repository.save(expected);

        flushAndClearSession();

        boolean interviewAssignmentExists = repository.existsByTargetIdAndActivityStateIn(application.getId(), singletonList(InterviewAssignmentState.CREATED));

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

        applicationRepository.saveAll(applications);

        List<InterviewAssignment> assignments = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED, AWAITING_FEEDBACK_RESPONSE, SUBMITTED_FEEDBACK_RESPONSE)
                .withTarget(applications.toArray(new Application[3]))
                .build(3);

        repository.saveAll(assignments);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

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
                .withState(CREATED)
                .withTarget(application)
                .build();

        repository.save(assignment);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

        assertEquals(0, count);
    }

    @Test
    public void countByTargetCompetitionIdAndActivityStateState_noApplications() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        flushAndClearSession();

        int count = repository.countByTargetCompetitionIdAndActivityStateIn(competition.getId(), NOTIFIED_INTERVIEW_ASSIGNMENT_STATES);

        assertEquals(0, count);
    }
}