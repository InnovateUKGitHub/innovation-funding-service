package org.innovateuk.ifs.review.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInvite;
import static org.innovateuk.ifs.review.resource.ReviewState.CREATED;
import static org.innovateuk.ifs.review.resource.ReviewState.WITHDRAWN;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReviewRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ReviewRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewInviteRepository reviewInviteRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Autowired
    @Override
    protected void setRepository(ReviewRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        loginPaulPlum();
    }

    @Test
    public void existsByParticipantUserAndTarget() {
        User user = newUser()
                .with(id(null))
                .withUid("foo")
                .build();

        userRepository.save(user);

        Application application = newApplication().with(id(null)).build();
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
        review.setProcessState(CREATED);
        repository.save(review);

        assertTrue(repository.existsByParticipantUserAndTargetAndActivityStateNot(user, application, ReviewState.WITHDRAWN)); // probably should be notExists if that's allowed
    }

    @Test
    public void existsByParticipantUserAndTarget_notExists() {
        User user = newUser()
                .with(id(null))
                .withUid("foo")
                .build();

        userRepository.save(user);

        Application application = newApplication().with(id(null)).build();
        applicationRepository.save(application);

        Application application2 = newApplication().with(id(null)).build();
        applicationRepository.save(application2);

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
        review.setProcessState(CREATED);
        repository.save(review);

        assertFalse(repository.existsByParticipantUserAndTargetAndActivityStateNot(user, application2, ReviewState.WITHDRAWN));
    }

    @Test
    public void existsByTargetIdAndActivityStateState() {

        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
                .withUid("foo")
                .build();

        userRepository.save(user);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
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
        review.setProcessState(CREATED);
        repository.save(review);


        assertTrue(repository.existsByTargetCompetitionIdAndActivityState(competition.getId(), ReviewState.CREATED));
    }

    @Test
    public void existsByTargetIdAndActivityStateState_notExists() {
        Competition competition = newCompetition().with(id(null)).build();
        Competition competition2 = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);
        competitionRepository.save(competition2);

        User user = newUser()
                .with(id(null))
                .withUid("foo")
                .build();

        userRepository.save(user);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
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
        review.setProcessState(CREATED);
        repository.save(review);


        assertFalse(repository.existsByTargetCompetitionIdAndActivityState(competition2.getId(), ReviewState.CREATED));
    }

    @Test
    public void notifiable() {

        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
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
        reviewParticipant.setStatus(ParticipantStatus.ACCEPTED);
        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
                .build();
        applicationRepository.save(application);

        assertTrue(repository.notifiable(competition.getId()));
    }

    @Test
    public void notifiable_assigned() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
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
        reviewParticipant.setStatus(ParticipantStatus.ACCEPTED);

        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
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
        review.setProcessState(CREATED);

        repository.save(review);

        assertFalse(repository.notifiable(competition.getId()));
    }

    @Test
    public void notifiable_assigned_withdrawn() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
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
        reviewParticipant.setStatus(ParticipantStatus.ACCEPTED);
        reviewParticipantRepository.save(reviewParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(true)
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
        review.setProcessState(WITHDRAWN);

        repository.save(review);

        assertTrue(repository.notifiable(competition.getId()));
    }
}