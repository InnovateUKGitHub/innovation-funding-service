package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.domain.Milestone;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competitionsetup.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competitionsetup.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInvite;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

public class ReviewControllerIntegrationTest extends BaseControllerIntegrationTest<ReviewController> {

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
    private ReviewInviteRepository reviewInviteRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Override
    public void setControllerUnderTest(ReviewController controller) {
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
                .withAssessmentReviewPanelStatus(false)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.assignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertTrue(application.isInAssessmentReviewPanel());
    }

    @Test
    public void unAssignApplication() throws Exception {
        application = newApplication()
                .withId(applicationId)
                .withAssessmentReviewPanelStatus(true)
                .build();
        applicationRepository.save(application);
        RestResult<Void> result = controller.unAssignApplication(application.getId());
        assertTrue(result.isSuccess());
        application = applicationRepository.findOne(applicationId);
        assertFalse(application.isInAssessmentReviewPanel());
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

        ReviewInvite reviewInvite = newReviewInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        reviewInviteRepository.save(reviewInvite);

        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);
        reviewParticipant.getInvite().open();
        reviewParticipant.acceptAndAssignUser(user);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        flushAndClearSession();

        controller.notifyAssessors(competition.getId()).getSuccess();

        assertTrue(reviewRepository.existsByTargetCompetitionIdAndActivityState(competition.getId(), ReviewState.PENDING));
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

        ReviewInvite reviewInvite = newReviewInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        reviewInviteRepository.save(reviewInvite);

        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);
        reviewParticipant.getInvite().open();
        reviewParticipant.acceptAndAssignUser(user);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
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

        ReviewInvite reviewInvite = newReviewInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        reviewInviteRepository.save(reviewInvite);

        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);
        reviewParticipant.getInvite().open();
        reviewParticipant.acceptAndAssignUser(user);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(user)
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        Review review =
                newReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        review.setProcessState(ReviewState.CREATED);
        reviewRepository.save(review);

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

        ReviewInvite reviewInvite = newReviewInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        reviewInviteRepository.save(reviewInvite);

        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);
        reviewParticipant.getInvite().open();
        reviewParticipant.acceptAndAssignUser(user);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(user)
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        Review review =
                newReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        review.setProcessState(ReviewState.WITHDRAWN);
        reviewRepository.save(review);

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

        ReviewInvite reviewInvite = newReviewInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser()
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();

        reviewInviteRepository.save(reviewInvite);

        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);
        reviewParticipant.getInvite().open();
        reviewParticipant.acceptAndAssignUser(assessor);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(assessor)
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        List<Review> assessmentReviews =
                newReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build(2);

        assessmentReviews.get(0).setProcessState(ReviewState.ACCEPTED);
        assessmentReviews.get(1).setProcessState(ReviewState.PENDING);
        reviewRepository.save(assessmentReviews.get(0));
        reviewRepository.save(assessmentReviews.get(1));

        flushAndClearSession();

        List<ReviewResource> reviews = controller.getReviews(assessor.getId(), competition.getId()).getSuccess();

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
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        Review review = newReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        review.setProcessState(ReviewState.PENDING);
        reviewRepository.save(review);

        flushAndClearSession();

        controller.acceptInvitation(review.getId()).getSuccess();

        assertEquals(ReviewState.ACCEPTED, reviewRepository.findOne(review.getId()).getProcessState());
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
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        Review review = newReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        review.setProcessState(ReviewState.REJECTED);
        reviewRepository.save(review);

        flushAndClearSession();

        controller.acceptInvitation(review.getId()).getSuccess();

        assertEquals(ReviewState.ACCEPTED, reviewRepository.findOne(review.getId()).getProcessState());
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
                .withInAssessmentReviewPanel(true)
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findByEmail(getPaulPlum().getEmail()).get())
                .withApplication(application)
                .withRole(Role.PANEL_ASSESSOR)
                .build();
        processRoleRepository.save(processRole);

        Review review = newReview()
                .with(id(null))
                .withTarget(application)
                .withParticipant(processRole)
                .build();
        review.setProcessState(ReviewState.PENDING);
        reviewRepository.save(review);

        flushAndClearSession();

        ReviewRejectOutcomeResource rejectOutcomeResource = newReviewRejectOutcomeResource()
                .withReason("comment")
                .build();

        controller.rejectInvitation(review.getId(), rejectOutcomeResource).getSuccess();

        assertEquals(ReviewState.REJECTED, reviewRepository.findOne(review.getId()).getProcessState());
    }
}