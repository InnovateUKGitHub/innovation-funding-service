package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;

@Rollback
public class ApplicationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationRepository> {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentInterviewPanelRepository assessmentInterviewPanelRepository;

    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByApplicationProcessActivityStateStateIn() {
        Collection<State> states = ApplicationState.submittedStates.stream().map(ApplicationState::getBackingState).collect(Collectors.toList());

        List<ApplicationState> applicationStates = Arrays.asList(ApplicationState.values());
        List<Application> applicationList = applicationStates.stream()
                .filter(state -> state != ApplicationState.IN_PANEL)
                .map(state -> createApplicationByState(state)).collect(Collectors
                        .toList());

        Long initial = repository.findByApplicationProcessActivityStateStateIn(states).count();

        repository.save(applicationList);
        Stream<Application> applications = repository.findByApplicationProcessActivityStateStateIn(states);

        assertEquals(initial + 5, applications.count());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);


        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withActivityState(activityState(ApplicationState.CREATED), activityState(ApplicationState.SUBMITTED))
                .with(id(null))
                .build(2);

        applicationRepository.save(applications);

        Pageable pageable = new PageRequest(0, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition.getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_noApplications() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        Pageable pageable = new PageRequest(0, 20);

        Page<Application> applications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition.getId(), pageable);

        assertEquals(0, applications.getTotalElements());
    }


    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_staged() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);


        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withActivityState(activityState(ApplicationState.SUBMITTED), activityState(ApplicationState.SUBMITTED))
                .with(id(null))
                .build(2);

        applicationRepository.save(applications);

        AssessmentInterviewPanel interviewPanel = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState(AssessmentInterviewPanelState.CREATED))
                .withTarget(applications.get(0))
                .build();

        assessmentInterviewPanelRepository.save(interviewPanel);

        Pageable pageable = new PageRequest(1, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition.getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_inviteSent() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withActivityState(activityState(ApplicationState.SUBMITTED), activityState(ApplicationState.SUBMITTED))
                .build(2);

        applicationRepository.save(applications);

        AssessmentInterviewPanel interviewPanel = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState(AssessmentInterviewPanelState.AWAITING_FEEDBACK_RESPONSE))
                .withTarget(applications.get(0))
                .build();

        assessmentInterviewPanelRepository.save(interviewPanel);

        Pageable pageable = new PageRequest(1, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition.getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    private Application createApplicationByState(ApplicationState applicationState) {
        Application application = newApplication()
                .with(id(null))
                .withApplicationState(applicationState)
                .build();
        application.getApplicationProcess().setActivityState(activityState(applicationState));
        return application;
    }

    private ActivityState activityState(ApplicationState applicationState) {
        return activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, applicationState.getBackingState());
    }

    private ActivityState activityState(AssessmentInterviewPanelState applicationState) {
        return activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_INTERVIEW_PANEL, applicationState.getBackingState());
    }
}