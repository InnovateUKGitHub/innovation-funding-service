package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.mapper.AssessmentFundingDecisionOutcomeMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentRejectOutcomeMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.Assessment} data.
 */
@Service
public class AssessmentServiceImpl extends BaseTransactionalService implements AssessmentService {
    protected static final Set<ApplicationState> SUBMITTED_APPLICATION_STATES = asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED);

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private AssessmentRejectOutcomeMapper assessmentRejectOutcomeMapper;

    @Autowired
    private AssessmentFundingDecisionOutcomeMapper assessmentFundingDecisionOutcomeMapper;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ReviewInviteRepository reviewInviteRepository;

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Override
    public ServiceResult<AssessmentResource> findById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<AssessmentResource> findAssignableById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getProcessState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<AssessmentResource> findRejectableById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getProcessState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId) {
        return serviceSuccess(simpleMap(assessmentRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId), assessmentMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndApplication(long userId, long applicationId) {
        return serviceSuccess(simpleMap(assessmentRepository.findByParticipantUserIdAndTargetId(userId, applicationId), assessmentMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByStateAndCompetition(AssessmentState state, long competitionId) {
        List<AssessmentResource> assessmentResources = simpleMap(assessmentRepository.findByActivityStateAndTargetCompetitionId(state, competitionId), assessmentMapper::mapToResource);
        return serviceSuccess(assessmentResources);
    }

    @Override
    public ServiceResult<Integer> countByStateAndCompetition(AssessmentState state, long competitionId) {
        return serviceSuccess(assessmentRepository.countByActivityStateAndTargetCompetitionId(state, competitionId));
    }

    @Override
    public ServiceResult<AssessmentTotalScoreResource> getTotalScore(long assessmentId) {
        return serviceSuccess(assessmentRepository.getTotalScore(assessmentId));
    }

    @Override
    @Transactional
    public ServiceResult<Void> recommend(long assessmentId, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.fundingDecision(found, assessmentFundingDecisionOutcomeMapper.mapToDomain(assessmentFundingDecision))) {
                return serviceFailure(ASSESSMENT_RECOMMENDATION_FAILED);
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId) {
        return serviceSuccess(new ApplicationAssessmentFeedbackResource(
                            assessmentRepository.findByTargetId(applicationId).stream()
                                    .map(Assessment::getFundingDecision)
                                    .filter(Objects::nonNull)
                                    .map(AssessmentFundingDecisionOutcome::getFeedback)
                                    .collect(Collectors.toList())
                )
        );
    }

    @Override
    public ServiceResult<ReviewKeyStatisticsResource> getAssessmentPanelKeyStatistics(long competitionId) {
        ReviewKeyStatisticsResource reviewKeyStatisticsResource = new ReviewKeyStatisticsResource();
        List<Long> assessmentPanelInviteIds = simpleMap(reviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        reviewKeyStatisticsResource.setApplicationsInPanel(getApplicationPanelAssignedCountStatistic(competitionId));
        reviewKeyStatisticsResource.setAssessorsAccepted(getParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, assessmentPanelInviteIds));
        reviewKeyStatisticsResource.setAssessorsPending(reviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, Collections.singleton(InviteStatus.SENT)));

        return serviceSuccess(reviewKeyStatisticsResource);
    }

    private int getApplicationPanelAssignedCountStatistic(long competitionId) {
        return applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_APPLICATION_STATES, "",  null,true).size();
    }

    @Override
    public ServiceResult<ReviewInviteStatisticsResource> getAssessmentPanelInviteStatistics(long competitionId) {
        ReviewInviteStatisticsResource statisticsResource = new ReviewInviteStatisticsResource();
        List<Long> assessmentPanelInviteIds = simpleMap(reviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        statisticsResource.setInvited(reviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        statisticsResource.setAccepted(getParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, assessmentPanelInviteIds));
        statisticsResource.setDeclined(getParticipantCountStatistic(competitionId, ParticipantStatus.REJECTED, assessmentPanelInviteIds));

        return serviceSuccess(statisticsResource);
    }

    private int getParticipantCountStatistic(long competitionId, ParticipantStatus status, List<Long> inviteIds) {
        return reviewParticipantRepository.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, PANEL_ASSESSOR, status, inviteIds);
    }

    @Override
    @Transactional
    public ServiceResult<Void> rejectInvitation(long assessmentId,
                                                AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId))
                .andOnSuccess(found -> {
                    if (!assessmentWorkflowHandler.rejectInvitation(found,
                            assessmentRejectOutcomeMapper.mapToDomain(assessmentRejectOutcomeResource))) {
                        return serviceFailure(ASSESSMENT_REJECTION_FAILED);
                    }
                    return serviceSuccess();
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> withdrawAssessment(long assessmentId) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.withdraw(found)) {
                return serviceFailure(ASSESSMENT_WITHDRAW_FAILED);
            }
            return serviceSuccess();
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> acceptInvitation(long assessmentId) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.acceptInvitation(found)) {
                return serviceFailure(ASSESSMENT_ACCEPT_FAILED);
            }
            return serviceSuccess();
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissionsResource) {
        List<Assessment> assessments = assessmentRepository.findAll(assessmentSubmissionsResource.getAssessmentIds());
        List<Error> failures = new ArrayList<>();
        Set<Long> foundAssessmentIds = new HashSet<>();

        assessments.forEach(assessment -> {
            foundAssessmentIds.add(assessment.getId());

            if (!assessmentWorkflowHandler.submit(assessment) || !assessment.isInState(AssessmentState.SUBMITTED)) {
                failures.add(new Error(ASSESSMENT_SUBMIT_FAILED, assessment.getId(), assessment.getTarget().getName()));
            }
        });

        failures.addAll(
                assessmentSubmissionsResource.getAssessmentIds().stream()
                        .filter(assessmentId -> !foundAssessmentIds.contains(assessmentId))
                        .map(assessmentId -> notFoundError(Assessment.class, assessmentId))
                        .collect(toList())
        );

        if (!failures.isEmpty()) {
            return serviceFailure(failures);
        }

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource) {
        return getAssessor(assessmentCreateResource.getAssessorId())
                .andOnSuccess(assessor -> getApplication(assessmentCreateResource.getApplicationId())
                        .andOnSuccess(application -> checkApplicationAssignable(assessor, application))
                        .andOnSuccess(application ->  createAssessment(assessor, application, ASSESSOR))
                );
    }


    private ServiceResult<AssessmentResource> createAssessment(User assessor, Application application, Role role) {

        ProcessRole processRole = getExistingOrCreateNewProcessRole(assessor, application, role);

        Assessment assessment = new Assessment(application, processRole);

        return serviceSuccess(assessmentRepository.save(assessment))
                .andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    private ProcessRole getExistingOrCreateNewProcessRole(User assessor, Application application, Role role) {
        ProcessRole processRole = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(assessor.getId(), singletonList(Role.ASSESSOR), application.getId());

        if (processRole == null) {
            processRole = new ProcessRole();
            processRole.setUser(assessor);
            processRole.setApplicationId(application.getId());
            processRole.setRole(role);
            processRole = processRoleRepository.save(processRole);
        }

        return processRole;
    }

    private ServiceResult<User> getAssessor(Long assessorId) {
        return find(userRepository.findByIdAndRoles(assessorId, ASSESSOR), notFoundError(User.class, ASSESSOR, assessorId));
    }

    private ServiceResult<Application> checkApplicationAssignable(User assessor, Application application) {
        boolean noAssessmentOrWithdrawn = assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessor.getId(), application.getId())
                .map(assessment -> assessment.getProcessState().equals(WITHDRAWN))
                .orElse(true);

        if (noAssessmentOrWithdrawn) {
            return serviceSuccess(application);
        }

        return serviceFailure(new Error(ASSESSMENT_CREATE_FAILED, assessor.getId(), application.getId()));
    }

    private ServiceResult<ActivityState> getAssessmentActivityState(AssessmentState assessmentState) {
        return find(
                activityStateRepository.findOneByActivityTypeAndState(
                        ActivityType.APPLICATION_ASSESSMENT,
                        assessmentState.getBackingState()
                ),
                notFoundError(ActivityState.class, assessmentState));
    }
}
