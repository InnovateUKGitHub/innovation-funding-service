package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.mapper.QuestionStatusMapper;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSIGNEE_SHOULD_BE_APPLICANT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Implements {@link QuestionStatusService}
 */
@Service
@Primary
public class QuestionStatusServiceImpl extends BaseTransactionalService implements QuestionStatusService {

    @Autowired
    private QuestionStatusMapper questionStatusMapper;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ApplicationValidationUtil validationUtil;

    @Override
    @Transactional
    public ServiceResult<List<ValidationMessages>> markAsComplete(final QuestionApplicationCompositeId ids,
                                                                  final long markedAsCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsCompleteById, true, true);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markAsCompleteNoValidate(QuestionApplicationCompositeId ids, long markedAsCompleteById) {
        return find(processRole(markedAsCompleteById), openApplication(ids.applicationId), getQuestionSupplier(ids.questionId))
                .andOnSuccess((markedAsCompleteBy, application, question)
                        -> setCompleteOnFindAndSuccess(markedAsCompleteBy, application, question, true, true));
    }

    @Override
    @Transactional
    public ServiceResult<List<ValidationMessages>> markAsInComplete(final QuestionApplicationCompositeId ids,
                                                                    final long markedAsInCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsInCompleteById, false, true);
    }

    @Override
    @Transactional
    public ServiceResult<List<ValidationMessages>> markTeamAsInComplete(final QuestionApplicationCompositeId ids,
                                                                        final long markedAsInCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsInCompleteById, false, true);
    }

    @Override
    @Transactional
    public ServiceResult<Void> assign(final QuestionApplicationCompositeId ids, final long assigneeId, final long assignedById) {
        return find(getQuestionSupplier(ids.questionId), openApplication(ids.applicationId), processRole(assigneeId), processRole(assignedById))
                .andOnSuccess((question, application, assignee, assignedBy) -> {

                    if (!isAssignableUser(ids.applicationId, assignee.getUser().getId())) {
                        return serviceFailure(ASSIGNEE_SHOULD_BE_APPLICANT);
                    }

                    QuestionStatus questionStatus = getQuestionStatusByApplicationIdAndAssigneeId(question, ids.applicationId, assigneeId);

                    if (questionStatus == null) {
                        questionStatus = new QuestionStatus(question, application, assignee, assignedBy, now());
                    } else {
                        questionStatus.setAssignee(assignee, assignedBy, ZonedDateTime.now());
                    }
                    questionStatusRepository.save(questionStatus);
                    return serviceSuccess();
                });
    }

    private Boolean isAssignableUser(Long applicationId, Long userId) {
        return userService.findAssignableUsers(applicationId).getSuccess().stream()
                .map(UserResource::getId)
                .anyMatch(allowedUserId -> allowedUserId.equals(userId));
    }

    @Override
    public ServiceResult<Set<Long>> getMarkedAsComplete(long applicationId,
                                                        long organisationId) {
        return find(application(applicationId)).andOnSuccess(application -> {

            List<Question> questions = questionRepository.findByCompetitionId(application.getCompetition().getId());
            List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(applicationId);

            Set<Long> markedAsCompleteQuestions = questions
                    .stream()
                    .filter(Question::isMarkAsCompletedEnabled)
                    .filter(q -> simpleAnyMatch(questionStatuses, qs ->
                            qs.getQuestion().getId().equals(q.getId()) &&
                                    ((q.hasMultipleStatuses() && isMarkedAsCompleteForOrganisation(qs, organisationId).orElse(false)) ||
                                            (!q.hasMultipleStatuses() && isMarkedAsCompleteForSingleStatus(qs).orElse(false)))
                    )).map(Question::getId).collect(Collectors.toSet());
            return serviceSuccess(markedAsCompleteQuestions);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateNotification(final long questionStatusId,
                                                  final boolean notify) {

        return find(questionStatusRepository.findById(questionStatusId), notFoundError(QuestionStatus.class, questionStatusId)).andOnSuccessReturnVoid(questionStatus -> {
            questionStatus.setNotified(notify);
            questionStatusRepository.save(questionStatus);
        });
    }

    @Override
    public ServiceResult<Boolean> isMarkedAsComplete(Question question, long applicationId, long organisationId) {
        if (question.hasMultipleStatuses()) {
            return serviceSuccess(isMarkedAsCompleteForOrganisation(question.getId(), applicationId, organisationId));
        } else {
            return serviceSuccess(isMarkedAsCompleteForSingleStatus(question.getId(), applicationId));
        }
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(long questionId, long applicationId) {
        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        List<QuestionStatusResource> resources = simpleMap(statuses, questionStatusMapper::mapToResource);
        return serviceSuccess(resources);
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> getQuestionStatusForOrganisationOnApplication(long questionId, long applicationId, long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(Long[] questionIds, long applicationId, long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdIsInAndApplicationId(Arrays.asList(questionIds), applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(long applicationId, long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<Optional<QuestionStatusResource>> findByApplicationAndMarkedAsCompleteByOrganisation(long questionId, long applicationId, long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteAndMarkedAsCompleteByOrganisationId(questionId, applicationId, true, organisationId);
        return serviceSuccess(questionStatuses.stream().findFirst().map(questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> findCompletedQuestionsByApplicationId(long applicationId) {
        return serviceSuccess(questionStatusRepository.findByApplicationId(applicationId).stream()
                .filter(qs -> qs.getMarkedAsComplete() != null)
                .filter(QuestionStatus::getMarkedAsComplete)
                .map(questionStatusMapper::mapToResource)
                .collect(toList()));
    }

    @Override
    public ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(long id) {
        return find(questionStatusRepository.findById(id), notFoundError(QuestionStatus.class, id)).andOnSuccessReturn(questionStatusMapper::mapToResource);
    }

    @Override
    public ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(long applicationId, long assigneeId) {
        return serviceSuccess(questionStatusRepository.countByApplicationIdAndAssigneeId(applicationId, assigneeId));
    }

    private Boolean isMarkedAsCompleteForOrganisation(long questionId, long applicationId, long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteAndMarkedAsCompleteByOrganisationId(questionId, applicationId, true, organisationId);
        return !questionStatuses.isEmpty();
    }

    private Boolean isMarkedAsCompleteForSingleStatus(long questionId, long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsComplete(questionId, applicationId, true);
        return !questionStatuses.isEmpty();
    }

    private Optional<Boolean> isMarkedAsCompleteForOrganisation(QuestionStatus questionStatus, long organisationId) {
        Boolean markedAsComplete = null;
        if (questionStatus.getMarkedAsCompleteBy() != null &&
                questionStatus.getMarkedAsCompleteBy().getOrganisationId().equals(organisationId)) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }

    private Optional<Boolean> isMarkedAsCompleteForSingleStatus(QuestionStatus questionStatus) {
        Boolean markedAsComplete = null;
        if (questionStatus != null) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }

    private List<QuestionStatus> filterByOrganisationIdIfHasMultipleStatuses(final List<QuestionStatus> questionStatuses, long organisationId) {
        return questionStatuses.stream().
                filter(
                        qs -> !qs.getQuestion().hasMultipleStatuses() ||
                                processRoleIsInOrganisation(qs.getAssignee(), organisationId) ||
                                processRoleIsInOrganisation(qs.getMarkedAsCompleteBy(), organisationId)
                )
                .collect(toList());
    }

    private static boolean processRoleIsInOrganisation(ProcessRole pr, long organisationId) {
        return pr != null && pr.getOrganisationId() == organisationId;
    }

    protected ServiceResult<List<ValidationMessages>> setComplete(long questionId, long applicationId, long processRoleId, boolean markAsComplete, boolean updateApplicationCompleteStatus) {
        return find(processRole(processRoleId), openApplication(applicationId), getQuestionSupplier(questionId))
                .andOnSuccess((markedAsCompleteBy, application, question) -> {
                            List<ValidationMessages> validation = validateApplicationQuestion(markAsComplete, question, application, processRoleId);
                            if (validation.isEmpty()) {
                                setCompleteOnFindAndSuccess(markedAsCompleteBy, application, question, markAsComplete, updateApplicationCompleteStatus);
                            }
                            return serviceSuccess(validation);
                        }
                );
    }

    private List<ValidationMessages> validateApplicationQuestion(boolean markAsComplete, Question question, Application application, long processRoleId) {
        if (markAsComplete) {
            return validationUtil.isQuestionValid(question, application, processRoleId);
        } else {
            return new ArrayList<>();
        }
    }

    private Optional<QuestionStatus> getQuestionStatus(Question question, Application application, ProcessRole markedAsCompleteBy) {
        if (question.hasMultipleStatuses()) {
            // The current user might not have a QuestionStatus, but maybe someone else in his organisation does? If so, use that one.
            List<ProcessRole> otherOrganisationMembers = processRoleRepository.findByApplicationIdAndOrganisationId(application.getId(), markedAsCompleteBy.getOrganisationId());
            return otherOrganisationMembers.stream()
                    .map(m -> getQuestionStatusByMarkedAsCompleteId(question, application.getId(), m.getId()))
                    .filter(Objects::nonNull)
                    .findFirst();
        } else {
            return Optional.ofNullable(getQuestionStatusByMarkedAsCompleteId(question, application.getId(), markedAsCompleteBy.getId()));
        }
    }

    private ServiceResult<Void> setCompleteOnFindAndSuccess(ProcessRole markedAsCompleteBy,
                                                                      Application application,
                                                                      Question question,
                                                                      boolean markAsComplete,
                                                                      boolean updateApplicationCompleteStatus) {
        QuestionStatus questionStatus = getQuestionStatus(question, application, markedAsCompleteBy).orElse(new QuestionStatus(question, application));

        if (markAsComplete) {
            questionStatus.markAsComplete(markedAsCompleteBy, now());
        } else {
            questionStatus.markAsInComplete();
        }

        questionStatusRepository.save(questionStatus);

        if (updateApplicationCompleteStatus) {
            applicationProgressService.updateApplicationProgress(application.getId()).getSuccess();
        }

        return serviceSuccess();
    }

    private QuestionStatus getQuestionStatusByApplicationIdAndAssigneeId(Question question, long applicationId, long assigneeId) {
        if (question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(question.getId(), applicationId, assigneeId);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus getQuestionStatusByMarkedAsCompleteId(Question question, long applicationId, long markedAsCompleteById) {
        if (question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(question.getId(), applicationId, markedAsCompleteById);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus findByQuestionIdAndApplicationId(long questionId, long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        if (questionStatuses != null && !questionStatuses.isEmpty()) {
            return questionStatuses.get(0);
        }
        return null;
    }

    private Supplier<ServiceResult<Question>> getQuestionSupplier(long questionId) {
        return () -> find(questionRepository.findById(questionId), notFoundError(Question.class, questionId));
    }
}