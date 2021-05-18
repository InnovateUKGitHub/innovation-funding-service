package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

public class AssessmentControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentController> {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Override
    protected void setControllerUnderTest(final AssessmentController controller) {
        this.controller = controller;
    }

    @Test
    public void findById() {
        Assessment assessment = setUpAssessment(getFelixWilson(), OPEN);

        loginFelixWilson();
        AssessmentResource result = controller.findById(assessment.getId()).getSuccess();
        assertNotNull(result);
        assertEquals(assessment.getId(), result.getId());
    }

    @Test
    public void findById_notFound() {
        long assessmentId = 999L;

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 999L)));
    }

    @Test
    public void findById_notTheAssessmentOwner() {
        Assessment assessment = setUpAssessment(getFelixWilson(), OPEN);

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findAssignableById() {
        Assessment assessment = setUpAssessment(getPaulPlum(), AssessmentState.PENDING);

        loginPaulPlum();
        AssessmentResource result = controller.findAssignableById(assessment.getId()).getSuccess();
        assertNotNull(result);
        assertEquals(assessment.getId(), result.getId());
    }

    @Test
    public void findAssignableById_notFound() {
        long assessmentId = 999L;

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 999L)));
    }

    @Test
    public void findAssignableById_notTheAssessmentOwner() {
        Assessment assessment = setUpAssessment(getFelixWilson(), AssessmentState.PENDING);

        loginSteveSmith();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findAssignableById_notAssignable() {
        Assessment assessment = setUpAssessment(getFelixWilson(), OPEN);

        loginFelixWilson();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findAssignableById_withdrawn() {
        Assessment assessment = setUpAssessment(getFelixWilson(), WITHDRAWN);

        loginFelixWilson();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessment
                .getId()))));
    }

    @Test
    public void findRejectableById() {
        Assessment assessment = setUpAssessment(getFelixWilson(), OPEN);

        loginFelixWilson();
        AssessmentResource result = controller.findRejectableById(assessment.getId()).getSuccess();
        assertNotNull(result);
        assertEquals(assessment.getId(), result.getId());
    }

    @Test
    public void findRejectableById_notFound() {
        long assessmentId = 999L;

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findRejectableById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 999L)));
    }

    @Test
    public void findRejectableById_notTheAssessmentOwner() {
        Assessment assessment = setUpAssessment(getFelixWilson(), AssessmentState.PENDING);

        loginSteveSmith();
        RestResult<AssessmentResource> result = controller.findRejectableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findRejectableById_notRejectable() {
        Assessment assessment = setUpAssessment(getFelixWilson(), SUBMITTED);

        loginFelixWilson();
        RestResult<AssessmentResource> result = controller.findRejectableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findRejectableById_withdrawn() {
        Assessment assessment = setUpAssessment(getFelixWilson(), WITHDRAWN);

        loginFelixWilson();
        RestResult<AssessmentResource> result = controller.findRejectableById(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessment
                .getId()))));
    }

    @Test
    public void findByUserAndCompetition_failure() {
        long userId = 3L;
        long competitionId = 1L;

        loginPaulPlum();
        RestResult<List<AssessmentResource>> result = controller.findByUserAndCompetition(userId, competitionId);
        assertTrue(result.getFailure().is(notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void findByUserAndCompetition_success() {
        long userId = 3L;
        long competitionId = 1L;

        Competition competition = newCompetition().withId(competitionId)
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .withAlwaysOpen(false)
                .build();

        assessmentParticipantRepository.save(newAssessmentParticipant()
                .with(id(null))
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newAssessmentInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser(userMapper.mapToDomain(getPaulPlum()))
                        .withHash("hash")
                        .withCompetition(competition)
                        .withInnovationArea(newInnovationArea().build())
                        .build())
                .withUser(userMapper.mapToDomain(getPaulPlum()))
                .build());

        loginPaulPlum();
        RestResult<List<AssessmentResource>> result = controller.findByUserAndCompetition(userId, competitionId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void countByStateAndCompetition() {
        Long competitionId = 1L;

        loginCompAdmin();
        RestResult<Integer> result = controller.countByStateAndCompetition(CREATED, competitionId);

        assertTrue(result.isSuccess());
        long count = result.getSuccess();
        assertEquals(1L, count);
    }

    @Test
    public void getTotalScore() {
        loginPaulPlum();

        AssessmentTotalScoreResource result = controller.getTotalScore(1L).getSuccess();
        assertEquals(Integer.valueOf(72), result.getTotalScoreGiven());
        assertEquals(Integer.valueOf(100), result.getTotalScorePossible());
    }

    @Test
    public void recommend() {
        long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccess();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback("Feedback")
                .withComment("Comment")
                .build();

        RestResult<Void> result = controller.recommend(assessmentResource.getId(), assessmentFundingDecisionOutcomeResource);
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccess();
        assertEquals(OPEN, assessmentResult.getAssessmentState());
        assertEquals(assessmentFundingDecisionOutcomeResource, assessmentResult.getFundingDecision());
    }

    @Test
    public void getApplicationFeedback() {
        long applicationId = 4L;

        AssessmentFundingDecisionOutcome outcome1 = new AssessmentFundingDecisionOutcome();
        outcome1.setComment("Test Comment 1");
        outcome1.setFeedback("Feedback 1");
        outcome1.setFundingConfirmation(FALSE);

        Assessment assessment1 = assessmentRepository.findById(2L).get();
        assessment1.setFundingDecision(outcome1);

        AssessmentFundingDecisionOutcome outcome2 = new AssessmentFundingDecisionOutcome();
        outcome2.setComment("Test Comment 2");
        outcome2.setFeedback("Feedback 2");
        outcome2.setFundingConfirmation(FALSE);

        Assessment assessment2 = assessmentRepository.findById(6L).get();
        assessment2.setFundingDecision(outcome2);

        assessmentRepository.saveAll(asList(assessment1, assessment2));
        flushAndClearSession();

        loginSteveSmith();

        RestResult<ApplicationAssessmentFeedbackResource> result = controller.getApplicationFeedback(applicationId);

        assertTrue(result.isSuccess());

        ApplicationAssessmentFeedbackResource feedbackResource = result.getSuccess();
        assertEquals(asList("Feedback 1", "Feedback 2"), feedbackResource.getFeedback());
    }

    @Test
    public void rejectInvitation() {
        long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccess();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("comment")
                .build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), assessmentRejectOutcomeResource);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertTrue(assessmentResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void rejectInvitation_eventNotAccepted() {
        long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccess();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), assessmentRejectOutcomeResource);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);

        assertEquals(assessmentResult.getErrors().get(0).getErrorKey(), GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.getErrorKey());

        // Now reject the assessment again
        assertTrue(controller.rejectInvitation(assessmentId, assessmentRejectOutcomeResource).isFailure());
    }

    @Test
    public void accept() {
        long assessmentId = 4L;
        Long processRole = 17L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findAssignableById(assessmentId).getSuccess();
        assertEquals(AssessmentState.PENDING, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        RestResult<Void> result = controller.acceptInvitation(assessmentResource.getId());
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccess();
        assertEquals(ACCEPTED, assessmentResult.getAssessmentState());
    }

    @Test
    public void withdrawAssessment() {
        long assessmentId = 4L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findAssignableById(assessmentId).getSuccess();
        assertEquals(AssessmentState.PENDING, assessmentResource.getAssessmentState());

        loginCompAdmin();
        RestResult<Void> result = controller.withdrawAssessment(assessmentResource.getId());
        assertTrue(result.isSuccess());

        loginPaulPlum();
        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertTrue(assessmentResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void withdrawCreatedAssessment() {
        long assessmentId = 9L;

        loginCompAdmin();
        RestResult<AssessmentResource> assessmentResource = controller.findById(assessmentId);
        assertTrue(assessmentResource.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));

        RestResult<Void> result = controller.withdrawAssessment(assessmentId);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertTrue(assessmentResult.getFailure().is(notFoundError(Assessment.class, assessmentId)));
    }

    private Assessment setUpAssessment(UserResource userResource, AssessmentState state) {
        User user = userRepository.findById(userResource.getId()).get();

        ProcessRole processRole = processRoleRepository.save(newProcessRole()
                .withUser(user)
                .build());

        return assessmentRepository.save(newAssessment()
                .with(id(null))
                .withProcessState(state)
                .withParticipant(processRole)
                .build());
    }
}