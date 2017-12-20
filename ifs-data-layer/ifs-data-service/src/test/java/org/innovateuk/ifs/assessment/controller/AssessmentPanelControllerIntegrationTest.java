package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelInvite;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentPanelInviteBuilder.newAssessmentPanelInvite;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPanelControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentPanelController> {

    private static final long applicationId = 2L;
    private static final long competitionId = 3L;

    private Application application;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentPanelInviteRepository assessmentPanelInviteRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    @Override
    public void setControllerUnderTest(AssessmentPanelController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() {
        loginCompAdmin();
    }

    @After
    public void clearDown() {
        flushAndClearSession();
    }

    @Test
    public void assignApplication() throws Exception {
        application = newApplication()
                .withId(applicationId)
                .withAssessmentPanelStatus(false)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.assignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertTrue(application.isInAssessmentPanel());
    }

    @Test
    public void unAssignApplication() throws Exception {
        application = newApplication()
                .withId(applicationId)
                .withAssessmentPanelStatus(true)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.unAssignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertFalse(application.isInAssessmentPanel());
    }

    @Test
    public void notifyAssessors() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Milestone milestone = newMilestone()
                .with(id(null))
                .withCompetition(competition)
                .withType(MilestoneType.ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.parse("2017-12-18T12:00:00+00:00"))
                .build();
        milestoneRepository.save(milestone);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        AssessmentPanelInvite competitionAssessmentInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(competitionAssessmentInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(competitionAssessmentInvite);
        assessmentPanelParticipant.getInvite().open();
        assessmentPanelParticipant.acceptAndAssignUser(user);

        assessmentPanelParticipantRepository.save(assessmentPanelParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        flushAndClearSession();

        controller.notifyAssessors(competition.getId()).getSuccessObjectOrThrowException();

        assertTrue(assessmentReviewRepository.existsByTargetCompetitionIdAndActivityStateState(competition.getId(), State.PENDING));
    }

    @Test
    public void isPendingReviewNotifications() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Milestone milestone = newMilestone()
                .with(id(null))
                .withCompetition(competition)
                .withType(MilestoneType.ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.parse("2017-12-18T12:00:00+00:00"))
                .build();
        milestoneRepository.save(milestone);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        AssessmentPanelInvite competitionAssessmentInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(competitionAssessmentInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(competitionAssessmentInvite);
        assessmentPanelParticipant.getInvite().open();
        assessmentPanelParticipant.acceptAndAssignUser(user);

        assessmentPanelParticipantRepository.save(assessmentPanelParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        flushAndClearSession();

        assertTrue(controller.isPendingReviewNotifications(competition.getId()).getSuccessObjectOrThrowException());
    }

    @Test
    public void isPendingReviewNotifications_reviewExists() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Milestone milestone = newMilestone()
                .with(id(null))
                .withCompetition(competition)
                .withType(MilestoneType.ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.parse("2017-12-18T12:00:00+00:00"))
                .build();
        milestoneRepository.save(milestone);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        AssessmentPanelInvite competitionAssessmentInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(competitionAssessmentInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(competitionAssessmentInvite);
        assessmentPanelParticipant.getInvite().open();
        assessmentPanelParticipant.acceptAndAssignUser(user);

        assessmentPanelParticipantRepository.save(assessmentPanelParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(user)
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
        assessmentReviewRepository.save(assessmentReview);

        flushAndClearSession();

        assertFalse(controller.isPendingReviewNotifications(competition.getId()).getSuccessObjectOrThrowException());
    }

    @Test
    public void isPendingReviewNotifications_withdrawnReviewExists() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Milestone milestone = newMilestone()
                .with(id(null))
                .withCompetition(competition)
                .withType(MilestoneType.ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.parse("2017-12-18T12:00:00+00:00"))
                .build();
        milestoneRepository.save(milestone);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        AssessmentPanelInvite competitionAssessmentInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(competitionAssessmentInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(competitionAssessmentInvite);
        assessmentPanelParticipant.getInvite().open();
        assessmentPanelParticipant.acceptAndAssignUser(user);

        assessmentPanelParticipantRepository.save(assessmentPanelParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(user)
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.WITHDRAWN));
        assessmentReviewRepository.save(assessmentReview);

        flushAndClearSession();

        assertTrue(controller.isPendingReviewNotifications(competition.getId()).getSuccessObjectOrThrowException());
    }

    @Test
    public void isPendingReviewNotifications_noneExist() {
        assertFalse(controller.isPendingReviewNotifications(competitionId).getSuccessObjectOrThrowException());
    }
}