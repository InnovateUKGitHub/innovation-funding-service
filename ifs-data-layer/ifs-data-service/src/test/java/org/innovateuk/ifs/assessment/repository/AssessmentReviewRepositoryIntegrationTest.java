package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentInvite;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.repository.CompetitionAssessmentInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.CompetitionAssessmentInviteBuilder.newCompetitionAssessmentInvite;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentReviewRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentReviewRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionAssessmentInviteRepository competitionAssessmentInviteRepository;
    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    @Override
    protected void setRepository(AssessmentReviewRepository repository) {
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
                .withRole(UserRoleType.ASSESSOR_PANEL)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
        repository.save(assessmentReview);

        assertTrue(repository.existsByParticipantUserAndTarget(user, application)); // probably should be notExists if that's allowed
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
                .withRole(UserRoleType.ASSESSOR_PANEL)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
        repository.save(assessmentReview);

        assertFalse(repository.existsByParticipantUserAndTarget(user, application2));
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
                .withRole(UserRoleType.ASSESSOR_PANEL)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
        repository.save(assessmentReview);


        assertTrue(repository.existsByTargetCompetitionIdAndActivityStateState(competition.getId(), State.CREATED));
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
                .withRole(UserRoleType.ASSESSOR_PANEL)
                .build();
        processRoleRepository.save(processRole);

        AssessmentReview assessmentReview =
                newAssessmentReview()
                        .with(id(null))
                        .withParticipant(processRole)
                        .withTarget(application)
                        .build();
        assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
        repository.save(assessmentReview);


        assertFalse(repository.existsByTargetCompetitionIdAndActivityStateState(competition2.getId(), State.CREATED));
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

        CompetitionAssessmentInvite competitionAssessmentInvite = newCompetitionAssessmentInvite()
                .with(id(null))
                .withCompetition(competition)
                .withUser(user)
                .withEmail("tom@poly.io")
                .withStatus(InviteStatus.SENT)
                .withName("tom baldwin")
                .build();
        competitionAssessmentInviteRepository.save(competitionAssessmentInvite);

        CompetitionAssessmentParticipant competitionAssessmentParticipant = new CompetitionAssessmentParticipant(competitionAssessmentInvite);
        competitionAssessmentParticipant.setStatus(ParticipantStatus.ACCEPTED);
        competitionParticipantRepository.save(competitionAssessmentParticipant);

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentPanel(true)
                .build();
        applicationRepository.save(application);

        assertTrue(repository.notifiable(competition.getId()));
    }
}