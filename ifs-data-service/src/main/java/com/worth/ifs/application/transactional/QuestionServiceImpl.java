package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.transactional.FormInputTypeService;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;
import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class QuestionServiceImpl extends BaseTransactionalService implements QuestionService {

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FormInputTypeService formInputTypeService;

    @Autowired
    private QuestionStatusMapper questionStatusMapper;

    @Override
    public ServiceResult<Question> getQuestionById(final Long id) {
        return getQuestion(id);
    }

    @Override
    public ServiceResult<Void> markAsComplete(final Long questionId,
                                              final Long applicationId,
                                              final Long markedAsCompleteById) {
        return setComplete(questionId, applicationId, markedAsCompleteById, true);
    }

    @Override
    public ServiceResult<Void> markAsInComplete(final Long questionId,
                                                final Long applicationId,
                                                final Long markedAsInCompleteById) {
        return setComplete(questionId, applicationId, markedAsInCompleteById, false);
    }

    @Override
    public ServiceResult<Void> assign(final Long questionId, final Long applicationId, final Long assigneeId, final Long assignedById) {

        return find(question(questionId), application(applicationId), processRole(assigneeId), processRole(assignedById)).andOnSuccess((question, application, assignee, assignedBy) -> {

            QuestionStatus questionStatus = getQuestionStatusByApplicationIdAndAssigneeId(question, applicationId, assigneeId);

            if (questionStatus == null) {
                questionStatus = new QuestionStatus(question, application, assignee, assignedBy, now());
            } else {
                questionStatus.setAssignee(assignee, assignedBy, LocalDateTime.now());
            }
            questionStatusRepository.save(questionStatus);
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                                        Long organisationId) {
        return find(application(applicationId)).andOnSuccess(application -> {

            List<Question> questions = questionRepository.findByCompetitionId(application.getCompetition().getId());
            Set<Long> markedAsCompleteQuestions = questions
                    .stream()
                    .filter(q -> q.isMarkAsCompletedEnabled() && questionStatusRepository.findByQuestionIdAndApplicationId(q.getId(), applicationId)
                            .stream()
                            .anyMatch(qs ->
                                    (q.hasMultipleStatuses() && isMarkedAsCompleteForOrganisation(qs, organisationId).orElse(Boolean.FALSE)) ||
                                            (!q.hasMultipleStatuses() && isMarkedAsCompleteForSingleStatus(qs).orElse(Boolean.FALSE))))
                    .map(Question::getId).collect(Collectors.toSet());
            return serviceSuccess(markedAsCompleteQuestions);
        });
    }

    @Override
    public ServiceResult<Void> updateNotification(final Long questionStatusId,
                                                  final Boolean notify) {

        return find(questionStatusRepository.findOne(questionStatusId), notFoundError(QuestionStatus.class, questionStatusId)).andOnSuccessReturnVoid(questionStatus -> {
            questionStatus.setNotified(notify);
            questionStatusRepository.save(questionStatus);
        });
    }

    @Override
    public ServiceResult<List<Question>> findByCompetition(final Long competitionId) {
        return serviceSuccess(questionRepository.findByCompetitionId(competitionId));
    }

    // TODO DW - INFUND-1555 - in situation where next / prev question not found, should this be a 404?
    @Override
    public ServiceResult<Question> getNextQuestion(final Long questionId) {

        return find(question(questionId)).andOnSuccess(question -> {

            // retrieve next question within current section
            Question nextQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                    question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

            // retrieve next question in following section
            if (nextQuestion == null) {
                nextQuestion = getNextQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
            }

            // retrieve next question in any other section, but with higher priority
            if (nextQuestion == null) {
                nextQuestion = questionRepository.findFirstByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(
                        question.getCompetition().getId(), question.getPriority());
            }

            return serviceSuccess(nextQuestion);
        });
    }

    // TODO DW - INFUND-1555 - in situation where next / prev question not found, should this be a 404?
    @Override
    public ServiceResult<Question> getPreviousQuestionBySection(final Long sectionId) {
        return sectionService.getById(sectionId).andOnSuccessReturn(section -> {

            if (section.getParentSection() != null) {
                SectionResource previousSection = sectionService.getPreviousSection(section).getSuccessObjectOrNull();
                if (previousSection != null) {
                    Optional<Question> lastQuestionInSection = previousSection.getQuestions()
                            .stream()
                            .map(questionRepository::findOne)
                            .max(comparing(Question::getPriority));
                    return lastQuestionInSection.orElse(null);
                }
            }
            return null;
        });
    }

    // TODO DW - INFUND-1555 - in situation where next / prev question not found, should this be a 404?
    @Override
    public ServiceResult<Question> getNextQuestionBySection(final Long sectionId) {

        return sectionService.getById(sectionId).andOnSuccessReturn(section -> {

            if (section.getParentSection() != null) {
                SectionResource nextSection = sectionService.getNextSection(section).getSuccessObjectOrNull();
                if(nextSection!=null) {
                    Optional<Question> firstQuestionInSection = nextSection.getQuestions()
                            .stream()
                            .map(questionRepository::findOne)
                            .min(comparing(Question::getPriority));
                    return firstQuestionInSection.orElse(null);
                }
            }
            return null;
        });
    }

    @Override
    public ServiceResult<Question> getPreviousQuestion(final Long questionId) {

        return find(question(questionId)).andOnSuccess(question -> {

            Question previousQuestion = null;
            if (question != null) {
                previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                        question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

                if (previousQuestion == null) {
                    previousQuestion = getPreviousQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
                }
            }

            return serviceSuccess(previousQuestion);
        });
    }

    @Override
    public ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId) {
        if (question.hasMultipleStatuses()) {
            return serviceSuccess(isMarkedAsCompleteForOrganisation(question.getId(), applicationId, organisationId));
        } else {
            return serviceSuccess(isMarkedAsCompleteForSingleStatus(question.getId(), applicationId));
        }
    }

    @Override
    public ServiceResult<List<QuestionStatus>> getQuestionStatusByApplicationIdAndAssigneeId(Long questionId, Long applicationId) {
        return serviceSuccess(questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(Long[] questionIds, Long applicationId, Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdIsInAndApplicationId(Arrays.asList(questionIds), applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(applicationId);
        return serviceSuccess(simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper::mapToResource));
    }

    @Override
    public ServiceResult<QuestionStatus> getQuestionStatusResourceById(Long id) {
        return find(questionStatusRepository.findOne(id), notFoundError(QuestionStatus.class, id));
    }

    @Override
    public ServiceResult<Question> getQuestionByFormInputType(String formInputTypeTitle) {
        return getOnlyFormInputTypeByTitle(formInputTypeTitle).andOnSuccessReturn(inputType -> inputType.getFormInput().get(0).getQuestion());
    }

    private ServiceResult<Void> setComplete(Long questionId, Long applicationId, Long processRoleId, boolean markAsComplete) {

        return find(processRole(processRoleId), application(applicationId), question(questionId)).andOnSuccess((markedAsCompleteBy, application, question) -> {

            QuestionStatus questionStatus = getQuestionStatusByMarkedAsCompleteId(question, applicationId, processRoleId);
            if (questionStatus == null) {
                questionStatus = new QuestionStatus(question, application, markedAsCompleteBy, markAsComplete);
            } else if (markAsComplete) {
                questionStatus.markAsComplete();
            } else {
                questionStatus.markAsInComplete();
            }
            questionStatusRepository.save(questionStatus);
            return serviceSuccess();
        });
    }

    private Question getNextQuestionBySection(Long section, Long competitionId) {
        SectionResource nextSection = sectionService.getNextSection(section).getSuccessObject();
        if (nextSection != null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(competitionId, nextSection.getId());
        }
        return null;

    }

    private Question getPreviousQuestionBySection(Long section, Long competitionId) {
        SectionResource previousSection = sectionService.getPreviousSection(section).getSuccessObject();

        if (previousSection != null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(competitionId, previousSection.getId());
        }
        return null;
    }

    private QuestionStatus getQuestionStatusByApplicationIdAndAssigneeId(Question question, Long applicationId, Long assigneeId) {
        if (question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(question.getId(), applicationId, assigneeId);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus getQuestionStatusByMarkedAsCompleteId(Question question, Long applicationId, Long markedAsCompleteById) {
        if (question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(question.getId(), applicationId, markedAsCompleteById);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus findByQuestionIdAndApplicationId(Long questionId, Long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        if (questionStatuses != null && !questionStatuses.isEmpty()) {
            return questionStatuses.get(0);
        }
        return null;
    }

    private ServiceResult<FormInputType> getOnlyFormInputTypeByTitle(String formInputTypeTitle) {
        return getFormInputTypesByTitle(formInputTypeTitle).andOnSuccess(types -> getOnlyElementOrFail(types));
    }

    private Boolean isMarkedAsCompleteForOrganisation(Long questionId, Long applicationId, Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        for (QuestionStatus questionStatus : questionStatuses) {
            Optional<Boolean> markedAsComplete = isMarkedAsCompleteForOrganisation(questionStatus, organisationId);
            if (markedAsComplete.isPresent()) {
                return markedAsComplete.get();
            }
        }
        return Boolean.FALSE;
    }

    private Boolean isMarkedAsCompleteForSingleStatus(Long questionId, Long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        if (questionStatuses != null && !questionStatuses.isEmpty()) {
            return isMarkedAsCompleteForSingleStatus(questionStatuses.get(0)).orElse(Boolean.FALSE);
        }
        return Boolean.FALSE;
    }


    private Optional<Boolean> isMarkedAsCompleteForOrganisation(QuestionStatus questionStatus, Long organisationId) {
        Boolean markedAsComplete = null;
        if (questionStatus.getMarkedAsCompleteBy() != null &&
                questionStatus.getMarkedAsCompleteBy().getOrganisation().getId().equals(organisationId)) {
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

    private ServiceResult<List<FormInputType>> getFormInputTypesByTitle(String formInputTypeTitle) {
        return find(formInputTypeService.findByTitle(formInputTypeTitle), notFoundError(Question.class, formInputTypeTitle));
    }

    private List<QuestionStatus> filterByOrganisationIdIfHasMultipleStatuses(final List<QuestionStatus> questionStatuses, Long organisationId) {
        return questionStatuses.stream().
                filter(qs -> (!qs.getQuestion().hasMultipleStatuses() || (qs.getAssignee() != null && qs.getAssignee().getOrganisation().getId().equals(organisationId))))
                .collect(Collectors.toList());
    }

    private Supplier<ServiceResult<Question>> question(Long questionId) {
        return () -> getQuestion(questionId);
    }

    private ServiceResult<Question> getQuestion(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(Question.class, questionId));
    }
}
