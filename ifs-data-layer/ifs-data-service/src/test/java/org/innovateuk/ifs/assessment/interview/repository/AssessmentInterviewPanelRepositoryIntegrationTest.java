package org.innovateuk.ifs.assessment.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;

public class AssessmentInterviewPanelRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentInterviewPanelRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    @Override
    protected void setRepository(AssessmentInterviewPanelRepository repository) {
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
                AssessmentInterviewPanelState.CREATED.getBackingState()
        );

        AssessmentInterviewPanel expected = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState)
                .withTarget(application)
                .build();

        repository.save(expected);

        flushAndClearSession();

        Page<AssessmentInterviewPanel> actual = repository.findByTargetCompetitionIdAndActivityStateState(competition.getId(), activityState.getState(), pageable);

        assertEquals(expected.getId(), actual.getContent().get(0).getId());
    }
}