package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.mapper.AssessmentReviewMapper;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
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
import org.innovateuk.ifs.user.mapper.UserMapper;
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
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentPanelInviteBuilder.newAssessmentPanelInvite;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

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
    private AssessmentReviewMapper assessmentReviewMapper;

    @Autowired
    private UserMapper userMapper;

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

        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(assessmentPanelInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(assessmentPanelInvite);
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

        controller.notifyAssessors(competition.getId()).getSuccess();

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

        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(assessmentPanelInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(assessmentPanelInvite);
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

        assertTrue(controller.isPendingReviewNotifications(competition.getId()).getSuccess());
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

        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(assessmentPanelInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(assessmentPanelInvite);
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

        assertFalse(controller.isPendingReviewNotifications(competition.getId()).getSuccess());
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

        assertTrue(controller.isPendingReviewNotifications(competition.getId()).getSuccess());
    }

    @Test
    public void isPendingReviewNotifications_noneExist() {
        assertFalse(controller.isPendingReviewNotifications(competitionId).getSuccess());
    }

    @Test
    public void getAssessmentReviews() {
        loginPaulPlum();

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

        User assessor = userMapper.mapToDomain(getLoggedInUser());

        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser()
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        assessmentPanelInviteRepository.save(assessmentPanelInvite);

        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(assessmentPanelInvite);
        assessmentPanelParticipant.getInvite().open();
        assessmentPanelParticipant.acceptAndAssignUser(assessor);

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
                .withUser(assessor)
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        List<AssessmentReview> assessmentReviews =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build(2);

        assessmentReviews.get(0).setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.ACCEPTED));
        assessmentReviews.get(1).setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.PENDING));
        assessmentReviewRepository.save(assessmentReviews.get(0));
        assessmentReviewRepository.save(assessmentReviews.get(1));

        flushAndClearSession();

        List<AssessmentReviewResource> reviews = controller.getAssessmentReviews(assessor.getId(), competition.getId()).getSuccess();

        // Returned reviews ordered activity state id
        assertEquals(assessmentReviews.get(0).getId(), reviews.get(1).getId());
        assertEquals(assessmentReviews.get(1).getId(), reviews.get(0).getId());
    }

    @Test
    public void acceptInvitation() {
        loginPaulPlum();

        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview = newAssessmentReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.PENDING));
        assessmentReviewRepository.save(assessmentReview);

        flushAndClearSession();

        controller.acceptInvitation(assessmentReview.getId()).getSuccess();

        assertEquals(AssessmentReviewState.ACCEPTED, assessmentReviewRepository.findOne(assessmentReview.getId()).getActivityState());
    }

    @Test
    public void acceptInvitation_rejected() {
        loginPaulPlum();

        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview = newAssessmentReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.REJECTED));
        assessmentReviewRepository.save(assessmentReview);

        flushAndClearSession();

        controller.acceptInvitation(assessmentReview.getId()).getSuccess();

        assertEquals(AssessmentReviewState.ACCEPTED, assessmentReviewRepository.findOne(assessmentReview.getId()).getActivityState());
    }

    @Test
    public void rejectInvitation() {
        loginPaulPlum();

        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(UserRoleType.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview = newAssessmentReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.PENDING));
        assessmentReviewRepository.save(assessmentReview);

        flushAndClearSession();

        AssessmentReviewRejectOutcomeResource rejectOutcomeResource = newAssessmentReviewRejectOutcomeResource()
                .withReason("comment")
                .build();

        controller.rejectInvitation(assessmentReview.getId(), rejectOutcomeResource).getSuccess();

        assertEquals(AssessmentReviewState.REJECTED, assessmentReviewRepository.findOne(assessmentReview.getId()).getActivityState());
    }
}