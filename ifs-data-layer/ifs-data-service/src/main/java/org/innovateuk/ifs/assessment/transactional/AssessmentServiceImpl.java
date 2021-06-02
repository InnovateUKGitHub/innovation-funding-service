package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
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
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
    private CompetitionParticipantService competitionParticipantService;
    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Override
    public ServiceResult<AssessmentResource> findById(long id) {
        return find(assessmentRepository.findById(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<Boolean> existsByTargetId(long applicationId) {
        return serviceSuccess(assessmentRepository.existsByTargetId(applicationId));
    }

    @Override
    public ServiceResult<AssessmentResource> findAssignableById(long id) {
        return find(assessmentRepository.findById(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getProcessState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<AssessmentResource> findRejectableById(long id) {
        return find(assessmentRepository.findById(id), notFoundError(Assessment.class, id)).andOnSuccess(found -> {
            if (WITHDRAWN == found.getProcessState()) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN, id));
            }
            return serviceSuccess(assessmentMapper.mapToResource(found));
        });
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId) {

        List<CompetitionParticipantResource> competitionParticipantList = competitionParticipantService.getCompetitionAssessors(userId).getSuccess();

        competitionParticipantList = competitionParticipantList.stream()
                .filter(participant -> participant.getCompetitionId().equals(competitionId))
                .filter(participant -> participant.getStatus().equals(ParticipantStatusResource.ACCEPTED))
                .collect(toList());

        if (competitionParticipantList.isEmpty()) {
            return serviceFailure(notFoundError(Competition.class, competitionId));
        }

        return serviceSuccess(
                simpleMap(
                        sort(assessmentRepository.findByParticipantUserIdAndTargetCompetitionId(userId, competitionId),
                                comparing(Assessment::getProcessState)
                        ),
                        assessmentMapper::mapToResource
                )
        );
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
        return serviceSuccess(assessmentRepository.countByActivityStateAndTargetCompetitionIdAndParticipantUserStatusIn(state, competitionId, singletonList(UserStatus.ACTIVE)));
    }

    @Override
    public ServiceResult<Integer> countByStateAndAssessmentPeriodId(AssessmentState state, long assessmentPeriod) {
        return serviceSuccess(assessmentRepository.countByActivityStateAndTargetAssessmentPeriodIdAndParticipantUserStatusIn(state, assessmentPeriod, singletonList(UserStatus.ACTIVE)));
    }

    @Override
    public ServiceResult<AssessmentTotalScoreResource> getTotalScore(long assessmentId) {
        return serviceSuccess(assessmentRepository.getTotalScore(assessmentId));
    }

    @Override
    @Transactional
    public ServiceResult<Void> recommend(long assessmentId, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
        return find(assessmentRepository.findById(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
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
    @Transactional
    public ServiceResult<Void> rejectInvitation(long assessmentId,
                                                AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
        return find(assessmentRepository.findById(assessmentId), notFoundError(AssessmentRepository.class, assessmentId))
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
        return find(assessmentRepository.findById(assessmentId), notFoundError(AssessmentRepository.class, assessmentId))
                .andOnSuccess(found -> {
                    Application application = found.getTarget();
                    if (!assessmentWorkflowHandler.withdraw(found)) {
                        return serviceFailure(ASSESSMENT_WITHDRAW_FAILED);
                    }
                    boolean allAssessmentsWithdrawn = application.getAssessments()
                            .stream()
                            .filter(assessment -> !assessment.getId().equals(assessmentId))
                            .allMatch(assessment -> WITHDRAWN.equals(assessment.getProcessState()));
                    if (allAssessmentsWithdrawn) {
                        application.setAssessmentPeriod(null);
                    }
                    
                    return serviceSuccess();
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> unsubmitAssessment(long assessmentId) {
        return find(assessmentRepository.findById(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.unsubmitAssessment(found) || isAssessmentClosed(found)) {
                return serviceFailure(ASSESSMENT_UNSUBMIT_FAILED);
            }
            return serviceSuccess();
        });
    }

    private boolean isAssessmentClosed(Assessment assessment) {
        return assessment.getTarget().getCompetition().isAssessmentClosed();
    }

    @Override
    @Transactional
    public ServiceResult<Void> acceptInvitation(long assessmentId) {
        return find(assessmentRepository.findById(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.acceptInvitation(found)) {
                return serviceFailure(ASSESSMENT_ACCEPT_FAILED);
            }
            return serviceSuccess();
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissionsResource) {
        Iterable<Assessment> assessments = assessmentRepository.findAllById(assessmentSubmissionsResource.getAssessmentIds());
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
        return getUser(assessmentCreateResource.getAssessorId())
                .andOnSuccess(assessor -> getApplication(assessmentCreateResource.getApplicationId())
                        .andOnSuccess(application -> checkApplicationAssignable(assessor, application))
                        .andOnSuccess(application ->  createAssessment(assessor, application, ProcessRoleType.ASSESSOR, assessmentCreateResource.getAssessmentPeriodId()))
                );
    }

    @Override
    @Transactional
    public ServiceResult<List<AssessmentResource>> createAssessments(List<AssessmentCreateResource> assessmentCreateResource) {
        return aggregate(assessmentCreateResource.stream()
                .map(this::createAssessment)
                .collect(toList()));
    }


    private ServiceResult<AssessmentResource> createAssessment(User assessor, Application application, ProcessRoleType role, Long assessmentPeriodId) {

        AssessmentPeriod assessmentPeriod = getAssessmentPeriodFromIdOrDefault(application.getCompetition().getId(), assessmentPeriodId);
        if (assessmentPeriod != null) {
            application.setAssessmentPeriod(assessmentPeriod);
            applicationRepository.save(application);
        }

        ProcessRole processRole = getExistingOrCreateNewProcessRole(assessor, application, role);
        Assessment assessment = new Assessment(application, processRole);

        return serviceSuccess(assessmentRepository.save(assessment))
                     .andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    private AssessmentPeriod getAssessmentPeriodFromIdOrDefault(long competitionId, Long assessmentPeriodId) {
        AssessmentPeriod assessmentPeriod;
        if (assessmentPeriodId != null) {
            assessmentPeriod = assessmentPeriodRepository.findById(assessmentPeriodId).orElse(null);
        } else {
            assessmentPeriod = assessmentPeriodRepository.findFirstByCompetitionId(competitionId)
                    .orElse(null);
        }
        return assessmentPeriod;
    }

    private ProcessRole getExistingOrCreateNewProcessRole(User assessor, Application application, ProcessRoleType role) {
        ProcessRole processRole = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(assessor.getId(), EnumSet.of(ProcessRoleType.ASSESSOR), application.getId());

        if (processRole == null) {
            processRole = processRoleRepository.save(new ProcessRole(assessor, application.getId(), role));
        }

        return processRole;
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
}