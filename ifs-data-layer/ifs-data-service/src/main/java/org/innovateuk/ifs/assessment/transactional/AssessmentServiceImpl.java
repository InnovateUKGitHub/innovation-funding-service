package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.mapper.AssessmentFundingDecisionOutcomeMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentRejectOutcomeMapper;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentPanelApplicationInviteRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.workflow.resource.State.*;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.Assessment} data.
 */
@Service
public class AssessmentServiceImpl extends BaseTransactionalService implements AssessmentService {

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
    private AssessmentPanelApplicationInviteRepository assessmentPanelApplicationInviteRepository;

    @Override
    public ServiceResult<AssessmentResource> findById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<AssessmentResource> findAssignableById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getActivityState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<AssessmentResource> findRejectableById(long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getActivityState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId) {
        return serviceSuccess(simpleMap(assessmentRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId), assessmentMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByStateAndCompetition(AssessmentState state, long competitionId) {
        List<AssessmentResource> assessmentResources = simpleMap(assessmentRepository.findByActivityStateStateAndTargetCompetitionId(state.getBackingState(), competitionId), assessmentMapper::mapToResource);
        return serviceSuccess(assessmentResources);
    }

    @Override
    public ServiceResult<Integer> countByStateAndCompetition(AssessmentState state, long competitionId) {
        return serviceSuccess(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(state.getBackingState(), competitionId));
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
    public ServiceResult<AssessmentPanelKeyStatisticsResource> getAssessmentPanelKeyStatistics(long competitionId) {
        Set<State> ALL_ASSESSMENT_PANEL_INVITES = asLinkedSet(
                CREATED,
                PENDING,
                ACCEPTED,
                REJECTED,
                CONFLICT_OF_INTEREST);
        return serviceSuccess(new AssessmentPanelKeyStatisticsResource(
                (long)applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateState(competitionId, IN_PANEL),
                (long) assessmentPanelApplicationInviteRepository.findByActivityStateState(ACCEPTED).size(),
                (long) assessmentPanelApplicationInviteRepository.findByActivityStateStateIn(ALL_ASSESSMENT_PANEL_INVITES).size())
        );
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
                        .andOnSuccess(application -> getRole(UserRoleType.ASSESSOR)
                                .andOnSuccess(role -> getAssessmentActivityState(AssessmentState.CREATED)
                                        .andOnSuccess(activityState -> createAssessment(assessor, application, role, activityState))
                                )
                        )
                );
    }


    private ServiceResult<AssessmentResource> createAssessment(User assessor, Application application, Role role, ActivityState activityState) {

        ProcessRole processRole = getExistingOrCreateNewProcessRole(assessor, application, role);

        Assessment assessment = new Assessment(application, processRole);
        assessment.setActivityState(activityState);

        return serviceSuccess(assessmentRepository.save(assessment))
                .andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    private ProcessRole getExistingOrCreateNewProcessRole(User assessor, Application application, Role role) {
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(assessor.getId(), application.getId());

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
        return find(userRepository.findByIdAndRolesName(assessorId, UserRoleType.ASSESSOR.getName()), notFoundError(User.class, UserRoleType.ASSESSOR, assessorId));
    }

    private ServiceResult<Application> checkApplicationAssignable(User assessor, Application application) {
        boolean noAssessmentOrWithdrawn = assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessor.getId(), application.getId())
                .map(assessment -> assessment.getActivityState().equals(WITHDRAWN))
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
