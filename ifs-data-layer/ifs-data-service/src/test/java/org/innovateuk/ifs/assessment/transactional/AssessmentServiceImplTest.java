package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.mapper.AssessmentFundingDecisionOutcomeMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentRejectOutcomeMapper;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    private CompetitionParticipantService competitionParticipantService;

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private AssessmentMapper assessmentMapper;

    @Mock
    private AssessmentFundingDecisionOutcomeMapper assessmentFundingDecisionOutcomeMapper;

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Mock
    private AssessmentRejectOutcomeMapper assessmentRejectOutcomeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Mock
    private ApplicationService applicationService;

    @Test
    public void testExistsByTargetId() {
        long applicationId = 1L;
        when(assessmentRepository.existsByTargetId(applicationId)).thenReturn(true);
        Boolean hasAssessment = assessmentService.existsByTargetId(1L).getSuccess();
        assertTrue(hasAssessment);
        when(assessmentRepository.existsByTargetId(applicationId)).thenReturn(false);
        Boolean noAssessment = assessmentService.existsByTargetId(1L).getSuccess();
        assertFalse(noAssessment);
    }

    @Test
    public void findById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment().build();
        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentMapper.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findById(assessmentId).getSuccess();

        assertSame(expected, found);

        InOrder inOrder = inOrder(assessmentRepository, assessmentMapper);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentMapper).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withProcessState(PENDING)
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentMapper.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findAssignableById(assessmentId)
                .getSuccess();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepository, assessmentMapper);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentMapper).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById_withdrawn() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withProcessState(WITHDRAWN)
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findAssignableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepository).findById(assessmentId);
        verifyNoInteractions(assessmentMapper);
    }

    @Test
    public void findRejectableById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withProcessState(PENDING)
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentMapper.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findRejectableById(assessmentId)
                .getSuccess();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepository, assessmentMapper);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentMapper).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findRejectableById_withdrawn() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withProcessState(WITHDRAWN)
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findRejectableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepository).findById(assessmentId);
        verifyNoInteractions(assessmentMapper);
    }

    @Test
    public void findByUserAndCompetition() {
        long userId = 2L;
        long competitionId = 1L;

        List<Assessment> assessments = newAssessment().withProcessState(CREATED, WITHDRAWN).build(2);
        List<AssessmentResource> expected = sort(newAssessmentResource().withActivityState(WITHDRAWN, CREATED).build(2), comparing(AssessmentResource::getAssessmentState));
        List<CompetitionParticipantResource> competitionParticipantList = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .build(1);

        when(competitionParticipantService.getCompetitionAssessors(userId)).thenReturn(serviceSuccess(competitionParticipantList));
        when(assessmentRepository.findByParticipantUserIdAndTargetCompetitionId(userId, competitionId)).thenReturn(assessments);
        when(assessmentMapper.mapToResource(same(assessments.get(0)))).thenReturn(expected.get(0));
        when(assessmentMapper.mapToResource(same(assessments.get(1)))).thenReturn(expected.get(1));

        List<AssessmentResource> found = assessmentService.findByUserAndCompetition(userId, competitionId).getSuccess();

        assertEquals(expected, found);
        verify(assessmentRepository, only()).findByParticipantUserIdAndTargetCompetitionId(userId, competitionId);
    }

    @Test
    public void getTotalScore() {
        Long assessmentId = 1L;

        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(55)
                .withTotalScorePossible(100)
                .build();

        when(assessmentRepository.getTotalScore(assessmentId)).thenReturn(new AssessmentTotalScoreResource(55, 100));

        assertEquals(expected, assessmentService.getTotalScore(assessmentId).getSuccess());

        verify(assessmentRepository, only()).getTotalScore(assessmentId);
    }

    @Test
    public void recommend() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(OPEN)
                .build();

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome().build();
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource = newAssessmentFundingDecisionOutcomeResource().build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentFundingDecisionOutcomeMapper.mapToDomain(assessmentFundingDecisionOutcomeResource)).thenReturn(assessmentFundingDecisionOutcome);
        when(assessmentWorkflowHandler.fundingDecision(assessment, assessmentFundingDecisionOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentFundingDecisionOutcomeMapper, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentFundingDecisionOutcomeMapper).mapToDomain(assessmentFundingDecisionOutcomeResource);
        inOrder.verify(assessmentWorkflowHandler).fundingDecision(assessment, assessmentFundingDecisionOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void recommend_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(OPEN)
                .build();

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome().build();
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource = newAssessmentFundingDecisionOutcomeResource().build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentFundingDecisionOutcomeMapper.mapToDomain(assessmentFundingDecisionOutcomeResource)).thenReturn(assessmentFundingDecisionOutcome);
        when(assessmentWorkflowHandler.fundingDecision(assessment, assessmentFundingDecisionOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_RECOMMENDATION_FAILED));

        InOrder inOrder = inOrder(assessmentRepository, assessmentFundingDecisionOutcomeMapper, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentFundingDecisionOutcomeMapper).mapToDomain(assessmentFundingDecisionOutcomeResource);
        inOrder.verify(assessmentWorkflowHandler).fundingDecision(assessment, assessmentFundingDecisionOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationFeedback() {
        long applicationId = 1L;

        List<Assessment> expectedAssessments = newAssessment()
                .withFundingDecision(
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 1").build(),
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 2").build(),
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 3").build()
                )
                .build(3);

        ApplicationAssessmentFeedbackResource expectedFeedbackResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2", "Feedback 3"))
                .build();

        when(assessmentRepository.findByTargetId(applicationId)).thenReturn(expectedAssessments);

        ServiceResult<ApplicationAssessmentFeedbackResource> result = assessmentService.getApplicationFeedback(applicationId);

        verify(assessmentRepository).findByTargetId(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(expectedFeedbackResource, result.getSuccess());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(OPEN)
                .build();

        AssessmentRejectOutcome assessmentRejectOutcome = newAssessmentRejectOutcome().build();
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentRejectOutcomeMapper.mapToDomain(assessmentRejectOutcomeResource)).thenReturn(assessmentRejectOutcome);
        when(assessmentWorkflowHandler.rejectInvitation(assessment, assessmentRejectOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentRejectOutcomeMapper, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentRejectOutcomeMapper).mapToDomain(assessmentRejectOutcomeResource);
        inOrder.verify(assessmentWorkflowHandler).rejectInvitation(assessment, assessmentRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(OPEN)
                .build();

        AssessmentRejectOutcome assessmentRejectOutcome = newAssessmentRejectOutcome().build();
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentRejectOutcomeMapper.mapToDomain(assessmentRejectOutcomeResource)).thenReturn(assessmentRejectOutcome);
        when(assessmentWorkflowHandler.rejectInvitation(assessment, assessmentRejectOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_REJECTION_FAILED));

        InOrder inOrder = inOrder(assessmentRepository, assessmentRejectOutcomeMapper, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentRejectOutcomeMapper).mapToDomain(assessmentRejectOutcomeResource);
        inOrder.verify(assessmentWorkflowHandler).rejectInvitation(assessment, assessmentRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment() {
        Assessment assessment = newAssessment()
                .withProcessState(OPEN)
                .withApplication(newApplication().build())
                .build();
        assessment.getTarget().setAssessments(newArrayList(assessment));

        when(assessmentRepository.findById(assessment.getId())).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.withdraw(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).withdraw(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment_eventNotAccepted() {
        Assessment assessment = newAssessment()
                .withProcessState(OPEN)
                .build();

        when(assessmentRepository.findById(assessment.getId())).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.withdraw(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_WITHDRAW_FAILED));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).withdraw(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void unsubmitAssessment() {
        Competition competition = newCompetition()
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Assessment assessment = newAssessment()
                .withProcessState(SUBMITTED)
                .withApplication(application)
                .build();

        when(assessmentRepository.findById(assessment.getId())).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.unsubmitAssessment(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.unsubmitAssessment(assessment.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).unsubmitAssessment(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void unsubmitAssessment_eventNotAccepted() {
        Assessment assessment = newAssessment()
                .withProcessState(OPEN)
                .build();

        when(assessmentRepository.findById(assessment.getId())).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.unsubmitAssessment(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.unsubmitAssessment(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_UNSUBMIT_FAILED));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).unsubmitAssessment(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(OPEN)
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.acceptInvitation(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository).findById(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessState(PENDING)
                .build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentWorkflowHandler.acceptInvitation(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_ACCEPT_FAILED));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository, calls(1)).findById(assessmentId);
        inOrder.verify(assessmentWorkflowHandler, calls(1)).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();
        List<Assessment> assessments = newAssessment()
                .withId(1L, 2L)
                .withProcessState(READY_TO_SUBMIT)
                .build(2);

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepository.findAllById(assessmentSubmissions.getAssessmentIds())).thenReturn(assessments);

        when(assessmentWorkflowHandler.submit(assessments.get(0))).thenAnswer(invocation -> {
            assessments.get(0).setProcessState(SUBMITTED);
            return Boolean.TRUE;
        });
        when(assessmentWorkflowHandler.submit(assessments.get(1))).thenAnswer(invocation -> {
            assessments.get(1).setProcessState(SUBMITTED);
            return Boolean.TRUE;
        });

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository, calls(1)).findAllById(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessments.get(0));
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessments.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_eventNotAccepted() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(singletonList(1L))
                .build();

        Application application = new Application();
        application.setName("Test Application");

        Assessment assessment = newAssessment()
                .withId(1L)
                .withProcessState(PENDING)
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(1, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepository.findAllById(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandler.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0), new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository, calls(1)).findAllById(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFound() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        when(assessmentRepository.findAllById(assessmentSubmissions.getAssessmentIds())).thenReturn(emptyList());

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 1L), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository, calls(1)).findAllById(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, never()).submit(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFoundAndEventNotAccepted() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        Application application = newApplication()
                .withName("Test Application")
                .build();

        Assessment assessment = newAssessment()
                .withId(1L)
                .withProcessState(PENDING)
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepository.findAllById(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandler.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepository, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepository, calls(1)).findAllById(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void createAssessment() {
        Long assessorId = 1L;
        Long applicationId = 2L;
        Long competitionId = 3L;

        User user = newUser().withId(assessorId).build();
        Competition competition = newCompetition()
                .withId(competitionId)
                .withAlwaysOpen(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        Application application = newApplication().withCompetition(competition).withId(applicationId).build();

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();
        AssessmentPeriod assessmentPeriod = newAssessmentPeriod().build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(assessorId, singleton(ProcessRoleType.ASSESSOR), applicationId)).thenReturn(null);
        when(processRoleRepository.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentRepository.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapper.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);
        when(assessmentPeriodRepository.findFirstByCompetitionId(competitionId)).thenReturn(Optional.of(assessmentPeriod));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepository, applicationRepository,
                processRoleRepository, applicationService, assessmentRepository, assessmentMapper
        );

        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verify(applicationRepository).findById(applicationId);
        inOrder.verify(assessmentRepository).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verify(processRoleRepository).findOneByUserIdAndRoleInAndApplicationId(assessorId, singleton(ProcessRoleType.ASSESSOR), applicationId);
        inOrder.verify(processRoleRepository).save(expectedProcessRole);
        inOrder.verify(assessmentRepository).save(expectedAssessment);
        inOrder.verify(assessmentMapper).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();


        verify(assessmentPeriodRepository).findFirstByCompetitionId(competitionId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
        assertEquals(assessmentPeriod, application.getAssessmentPeriod());
    }

    @Test
    public void createAssessment_withAssessmentPeriod() {
        Long assessorId = 1L;
        Long applicationId = 2L;
        Long competitionId = 3L;
        Long assessmentPeriodId = 5L;

        User user = newUser().withId(assessorId).build();
        Competition competition = newCompetition()
                .withId(competitionId)
                .withAlwaysOpen(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        Application application = newApplication().withCompetition(competition).withId(applicationId).build();

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();
        AssessmentPeriod assessmentPeriod = newAssessmentPeriod().build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(assessorId, singleton(ProcessRoleType.ASSESSOR), applicationId)).thenReturn(null);
        when(processRoleRepository.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentPeriodRepository.findById(assessmentPeriodId)).thenReturn(Optional.of(assessmentPeriod));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .withAssessmentPeriodId(assessmentPeriodId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        assertTrue(serviceResult.isSuccess());
        assertEquals(assessmentPeriod, application.getAssessmentPeriod());
    }

    @Test
    public void createAssessment_existingProcessRole() {
        Long assessorId = 1L;
        Long applicationId = 2L;
        Long competitionId = 3L;

        User user = newUser().withId(assessorId).build();
        Competition competition = newCompetition()
                .withId(competitionId)
                .withAlwaysOpen(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        Application application = newApplication().withCompetition(competition).withId(applicationId).build();

        ProcessRole expectedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(expectedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(expectedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();
        AssessmentPeriod assessmentPeriod = newAssessmentPeriod().build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(assessorId, singleton(ProcessRoleType.ASSESSOR), applicationId)).thenReturn(expectedProcessRole);
        when(assessmentRepository.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapper.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);
        when(assessmentPeriodRepository.findFirstByCompetitionId(competitionId)).thenReturn(Optional.of(assessmentPeriod));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepository, applicationRepository,
                processRoleRepository, assessmentRepository, applicationService, assessmentMapper
        );

        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verify(applicationRepository).findById(applicationId);
        inOrder.verify(assessmentRepository).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verify(processRoleRepository).findOneByUserIdAndRoleInAndApplicationId(assessorId, singleton(ProcessRoleType.ASSESSOR), applicationId);
        inOrder.verify(assessmentRepository).save(expectedAssessment);
        inOrder.verify(assessmentMapper).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
    }


    @Test
    public void createAssessment_existingWithdrawnAssessment() {
        Long assessorId = 1L;
        Long applicationId = 2L;
        Long competitionId = 3L;

        User user = newUser().withId(assessorId).build();
        Competition competition = newCompetition()
                .withId(competitionId)
                .withAlwaysOpen(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        Application application = newApplication().withCompetition(competition).withId(applicationId).build();

        Assessment existingAssessment = newAssessment()
                .withProcessState(WITHDRAWN)
                .build();

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ProcessRoleType.ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withProcessState(CREATED)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        AssessmentPeriod assessmentPeriod = newAssessmentPeriod().build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.of(existingAssessment));
        when(processRoleRepository.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentRepository.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapper.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);
        when(assessmentPeriodRepository.findFirstByCompetitionId(competitionId)).thenReturn(Optional.of(assessmentPeriod));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepository, applicationRepository,
                processRoleRepository, applicationService, assessmentRepository, assessmentMapper
        );

        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verify(applicationRepository).findById(applicationId);
        inOrder.verify(processRoleRepository).save(expectedProcessRole);
        inOrder.verify(assessmentRepository).save(expectedAssessment);
        inOrder.verify(assessmentMapper).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
        assertEquals(assessmentPeriod, application.getAssessmentPeriod());
    }

    @Test
    public void createAssessment_noAssessor() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        when(userRepository.findById(assessorId)).thenReturn(Optional.empty());

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepository, applicationRepository);
        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_noApplication() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepository, applicationRepository);
        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verify(applicationRepository).findById(applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_existingAssessment() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();

        Assessment existingAssessment = newAssessment()
                .withProcessState(PENDING)
                .build();

        when(userRepository.findById(assessorId)).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(user.getId(), application.getId()))
                .thenReturn(Optional.of(existingAssessment));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepository, applicationRepository, assessmentRepository);
        inOrder.verify(userRepository).findById(assessorId);
        inOrder.verify(applicationRepository).findById(applicationId);
        inOrder.verify(assessmentRepository).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_CREATE_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }
}