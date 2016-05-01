package com.worth.ifs.assessment.transactional;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.transactional.ResponseService;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.resource.Feedback;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.security.FeedbackLookupStrategy;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.transactional.UsersRolesService;
import com.worth.ifs.util.EntityLookupCallbacks;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.CollectionFunctions.mapEntryValue;
import static com.worth.ifs.util.CollectionFunctions.pairsToMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.PairFunctions.*;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toMap;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 *
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    private static ToIntFunction<String> stringToInteger = score -> StringUtils.isNumeric(score) ? Integer.parseInt(score) : 0;

    @Autowired
    private FeedbackLookupStrategy feedbackLookupStrategy;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ResponseService responseService;

    @Autowired
    private UsersRolesService usersRolesService;

    @Autowired
    private AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    @Override
    public ServiceResult<Feedback> updateAssessorFeedback(Feedback.Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText) {

        return find(response(feedbackId.getResponseId()), role(ASSESSOR)).andOnSuccess((response, assessorRole) -> {

            Application application = response.getApplication();

            return getAssessorProcessRole(feedbackId.getAssessorUserId(), application.getId(), assessorRole).andOnSuccessReturn(assessorProcessRole -> {

                Feedback feedback = new Feedback().setResponseId(response.getId()).
                        setAssessorUserId(assessorProcessRole.getId()).
                        setValue(feedbackValue).
                        setText(feedbackText);

                AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(assessorProcessRole);
                responseFeedback.setAssessmentValue(feedback.getValue().orElse(null));
                responseFeedback.setAssessmentFeedback(feedback.getText().orElse(null));
                responseRepository.save(response);
                return feedback;
            });
        });
    }

    @Override
    public ServiceResult<Feedback> getFeedback(Feedback.Id id) {
        return serviceSuccess(feedbackLookupStrategy.getFeedback(id));
    }

    @Override
    public ServiceResult<Void> save(Assessment a) {
        assessmentRepository.save(a);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Assessment> saveAndGet(Assessment a) {
        return serviceSuccess(assessmentRepository.save(a));
    }

    @Override
    public ServiceResult<Assessment> getOne(Long id) {
        return find(assessmentRepository.findById(id), notFoundError(Assessment.class, id));
    }

    /**
     * Get's all the assessments by competition and assessor.
     * By 'All' is meant all the assessments whose invitation was not rejected.
     * Also, groups the assessments by first having the pending ones and only after the open/active/submitted.
     */
    @Override
    public ServiceResult<List<Assessment>> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId) {
        Set<String> states = AssessmentStates.getStates();
        states.remove(AssessmentStates.REJECTED.getState());
        return serviceSuccess(assessmentRepository.findByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusIn(assessorId, competitionId, states));
    }

    @Override
    public ServiceResult<Assessment> getOneByProcessRole(Long processRoleId) {
        return find(assessmentRepository.findOneByProcessRoleId(processRoleId), notFoundError(Assessment.class, processRoleId));
    }

    @Override
    public ServiceResult<Integer> getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        return serviceSuccess(assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatus(assessorId, competitionId, ApplicationStatusConstants.SUBMITTED.getName()));
    }

    @Override
    public ServiceResult<Integer> getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        // By 'assigned' is meant an assessment process not rejected
        return serviceSuccess(assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusNot(assessorId, competitionId, ApplicationStatusConstants.REJECTED.getName()));
    }

    @Override
    public ServiceResult<Score> getScore(Long id) {
        Assessment assessment = assessmentRepository.findById(id);
        Application application = assessment.getProcessRole().getApplication();
        List<Response> responses = responseService.findResponsesByApplication(application.getId()).getSuccessObjectOrThrowException();
        Competition competition = application.getCompetition();
        ProcessRole assessorProcessRole = assessment.getProcessRole();

        List<Question> questions = competition.getSections().stream().
                flatMap(section -> section.getQuestions().stream()).
                collect(Collectors.toList());

        List<Pair<Question, Optional<Response>>> questionsAndResponsePairs = questions.stream().
                map(question -> Pair.of(question, responses.stream().
                        filter(response -> response.getQuestion().getId().equals(question.getId())).
                        findFirst())).
                collect(Collectors.toList());

        Map<Question, Optional<Response>> questionsAndResponses =
                questionsAndResponsePairs.stream().collect(pairsToMap());

        Map<Response, AssessorFeedback> responsesAndFeedback = responses.stream().
                map(response -> Pair.of(response, response.getResponseAssessmentForAssessor(assessorProcessRole))).
                filter(rightPairIsPresent()).
                collect(toMap(leftPair(), presentRightPair()));

        Map<Long, AssessorFeedback> responseIdsAndFeedback = responsesAndFeedback.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

        int total = questionsAndResponses.entrySet().stream().
                filter(e -> e.getKey().getNeedingAssessorScore()).
                map(mapEntryValue()).
                map(response -> response.map(r -> Optional.ofNullable(responseIdsAndFeedback.get(r.getId()))).orElse(empty())).
                map(optionalFeedback -> optionalFeedback.map(AssessorFeedback::getAssessmentValue).orElse("0")).
                collect(summingInt(stringToInteger));

        int possible = questions.stream().
                filter(Question::getNeedingAssessorScore).
                collect(summingInt(q -> 10));

        return serviceSuccess(new Score(possible, total));
    }

    @Override
    public ServiceResult<Void> submitAssessment(Long assessorUserId, Long applicationId, String suitableValue, String suitableFeedback, String comments) {

        return find(() -> usersRolesService.getProcessRoleByUserIdAndApplicationId(assessorUserId, applicationId)).andOnSuccess(processRole -> 

            getOneByProcessRole(processRole.getId()).andOnSuccessReturnVoid(assessment -> {

                Assessment newAssessment = new Assessment();
                ProcessOutcome processOutcome = new ProcessOutcome();
                processOutcome.setOutcome(getRecommendedValueFromString(suitableValue).name());
                processOutcome.setDescription(suitableFeedback);
                processOutcome.setComment(comments);
                newAssessment.setProcessStatus(assessment.getProcessStatus());

                assessmentWorkflowEventHandler.recommend(processRole.getId(), newAssessment, processOutcome);
            })
        );
    }

    @Override
    public ServiceResult<Void> acceptAssessmentInvitation(Long processRoleId, Assessment updatedAssessment) {
        return getOneByProcessRole(processRoleId).andOnSuccessReturnVoid(existingAssessment -> {
            updatedAssessment.setProcessStatus(existingAssessment.getProcessStatus());
            assessmentWorkflowEventHandler.acceptInvitation(processRoleId, updatedAssessment);
        });
    }

    @Override
    public ServiceResult<Void> rejectAssessmentInvitation(Long processRoleId, ProcessOutcome processOutcome) {
        return getOneByProcessRole(processRoleId).andOnSuccessReturnVoid(existingAssessment -> {
            String currentProcessStatus = existingAssessment.getProcessStatus();
            assessmentWorkflowEventHandler.rejectInvitation(processRoleId, currentProcessStatus, processOutcome);
        });
    }

    // TODO DW - INFUND-1555 - should push ServiceResults up into handler
    @Override
    public ServiceResult<Void> submitAssessments(Set<Long> assessments) {

        for(Long assessmentId : assessments) {
            getOne(assessmentId).andOnSuccessReturnVoid(assessmentWorkflowEventHandler::submit);
        }
        return serviceSuccess();
    }

    private ServiceResult<ProcessRole> getAssessorProcessRole(Long assessorUserId, Long applicationId, Role assessorRole) {
        return getProcessRoleByUseridRoleAndApplicationId(assessorUserId, applicationId, assessorRole).
                andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail);
    }

    private ServiceResult<List<ProcessRole>> getProcessRoleByUseridRoleAndApplicationId(Long assessorUserId, Long applicationId, Role assessorRole) {
        return find(processRoleRepository.findByUserIdAndRoleAndApplicationId(assessorUserId, assessorRole, applicationId),
                notFoundError(ProcessRole.class, assessorUserId, assessorRole.getName(), applicationId));
    }

    private RecommendedValue getRecommendedValueFromString(String value) {
        switch (value) {
            case "yes":
                return RecommendedValue.YES;
            case "no":
                return RecommendedValue.NO;
            default:
                return RecommendedValue.EMPTY;
        }
    }
}
