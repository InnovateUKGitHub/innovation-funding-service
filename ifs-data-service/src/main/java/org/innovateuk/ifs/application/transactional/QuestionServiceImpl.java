package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.mapper.QuestionStatusMapper;
import org.innovateuk.ifs.application.mapper.SectionMapper;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZonedDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSIGNEE_SHOULD_BE_APPLICANT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuestionStatusMapper questionStatusMapper;

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    public ServiceResult<QuestionResource> getQuestionById(final Long id) {
        return getQuestionResource(id);
    }

    @Override
    public ServiceResult<List<ValidationMessages>> markAsComplete(final QuestionApplicationCompositeId ids,
                                              final Long markedAsCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsCompleteById, true);
    }

    @Override
    public ServiceResult<List<ValidationMessages>> markAsInComplete(final QuestionApplicationCompositeId ids,
                                                final Long markedAsInCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsInCompleteById, false);
    }

    @Override
    public ServiceResult<Void> assign(final QuestionApplicationCompositeId ids, final Long assigneeId, final Long assignedById) {
        return find(getQuestionSupplier(ids.questionId), openApplication(ids.applicationId), processRole(assigneeId), processRole(assignedById))
                .andOnSuccess((question, application, assignee, assignedBy) -> {

            if(!isAssignableUser(ids.applicationId, assignee.getUser().getId())) {
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
        return userService.findAssignableUsers(applicationId).getSuccessObjectOrThrowException().stream()
                .map(UserResource::getId)
                .anyMatch(allowedUserId -> allowedUserId.equals(userId));
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
    public ServiceResult<List<QuestionResource>> findByCompetition(final Long competitionId) {
        return serviceSuccess(questionsToResources(questionRepository.findByCompetitionId(competitionId)));
    }

    // TODO DW - INFUND-1555 - in situation where next / prev question not found, should this be a 404?
    @Override
    public ServiceResult<QuestionResource> getNextQuestion(final Long questionId) {

        return find(getQuestionSupplier(questionId)).andOnSuccess(question -> {

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

            return serviceSuccess(questionMapper.mapToResource(nextQuestion));
        });
    }

    @Override
    public ServiceResult<QuestionResource> getPreviousQuestionBySection(final Long sectionId) {
        return sectionService.getById(sectionId).andOnSuccess(section -> {

            if (section.getParentSection() != null) {
                Optional<SectionResource> previousSection = sectionService.getPreviousSection(section).getOptionalSuccessObject();
                if (previousSection != null) {
                    Optional<Question> lastQuestionInSection = previousSection.get().getQuestions()
                            .stream()
                            .map(questionRepository::findOne)
                            .max(comparing(Question::getPriority));
                    if(lastQuestionInSection.isPresent()){
                        return serviceSuccess(questionMapper.mapToResource(lastQuestionInSection.get()));
                    }
                }
            }
            return serviceFailure(notFoundError(QuestionResource.class, "getPreviousQuestionBySection", sectionId));
        });
    }

    @Override
    public ServiceResult<QuestionResource> getNextQuestionBySection(final Long sectionId) {

        return sectionService.getById(sectionId)
                .andOnSuccess(section -> {

                    if (section.getParentSection() != null) {
                        Optional<SectionResource> nextSection = sectionService.getNextSection(section).getOptionalSuccessObject();
                        if (nextSection.isPresent()) {
                            Optional<Question> firstQuestionInSection = nextSection.get().getQuestions()
                                    .stream()
                                    .map(questionRepository::findOne)
                                    .min(comparing(Question::getPriority));

                            if (firstQuestionInSection.isPresent()) {
                                return serviceSuccess(questionMapper.mapToResource(firstQuestionInSection.get()));
                            }
                        }
                    }
                    return serviceFailure(notFoundError(QuestionResource.class, "getNextQuestionBySection", sectionId));
                });
    }

    @Override
    public ServiceResult<QuestionResource> getPreviousQuestion(final Long questionId) {

        return find(getQuestionSupplier(questionId)).andOnSuccess(question -> {

            Question previousQuestion = null;
            if (question != null) {
                previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                        question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

                if (previousQuestion == null) {
                    previousQuestion = getPreviousQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
                }
            }
            if(previousQuestion == null){
                return serviceFailure(notFoundError(QuestionResource.class, "getPreviousQuestion", questionId));
            }else{
                return serviceSuccess(questionMapper.mapToResource(previousQuestion));
            }
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
	public ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(Long questionId, Long applicationId) {
    	List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        List<QuestionStatusResource> resources = simpleMap(statuses, questionStatusMapper::mapToResource);
        return serviceSuccess(resources);
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
    public ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(Long id) {
        return find(questionStatusRepository.findOne(id), notFoundError(QuestionStatus.class, id)).andOnSuccessReturn(questionStatusMapper::mapToResource);
    }

    @Override
    public ServiceResult<Question> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType) {
        List<Question> questions = questionRepository.findByCompetitionId(competitionId);
        Optional<Question> question = simpleFindFirst(questions, q -> {
            List<FormInput> activeFormInputs = simpleFilter(q.getFormInputs(), FormInput::getActive);
            return !activeFormInputs.isEmpty() && formInputType == activeFormInputs.get(0).getType();
        });
        if (question.isPresent()) {
            return serviceSuccess(question.get());
        } else {
            return serviceFailure(notFoundError(Question.class, competitionId, formInputType));
        }
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionResourceByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInpuType) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInpuType).andOnSuccessReturn(questionMapper::mapToResource);
    }

    @Override
    public ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId){
        return serviceSuccess(questionStatusRepository.countByApplicationIdAndAssigneeId(applicationId, assigneeId));
    }

    @Override
	public ServiceResult<List<QuestionResource>> getQuestionsBySectionIdAndType(
			Long sectionId, QuestionType type) {
		return getSection(sectionId).andOnSuccessReturn(section -> questionsOfType(section, type));
	}

    @Override
    public ServiceResult<QuestionResource> save(QuestionResource questionResource) {
        Question questionUpdated = questionRepository.save(questionMapper.mapToDomain(questionResource));
        return serviceSuccess(questionMapper.mapToResource(questionUpdated));
    }

    @Override
    public ServiceResult<List<QuestionResource>> getQuestionsByAssessmentId(Long assessmentId) {
        return find(getAssessment(assessmentId)).andOnSuccess(assessment -> {
            Application application = applicationRepository.findOne(assessment.getParticipant().getApplicationId());
            return sectionService.getByCompetitionIdVisibleForAssessment(application.getCompetition().getId())
                    .andOnSuccessReturn(sections -> sections.stream().map(sectionMapper::mapToDomain).flatMap(section -> section.getQuestions().stream()).map(questionMapper::mapToResource).collect(toList()));
        });
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionByIdAndAssessmentId(Long questionId, Long assessmentId) {
        return find(getAssessment(assessmentId), getQuestionSupplier(questionId)).andOnSuccess((assessment, question) -> {
            if (question.getCompetition().getId().equals(assessment.getTarget().getCompetition().getId())) {
                return serviceSuccess(questionMapper.mapToResource(question));
            }
            return serviceFailure(notFoundError(Question.class, questionId, assessmentId));
        });
    }

    private List<QuestionResource> questionsOfType(Section section, QuestionType type) {
    	Stream<Question> sectionQuestionsStream = section.getQuestions().stream();
    	Stream<Question> childSectionsQuestionsStream = section.getChildSections().stream().flatMap(s -> s.getQuestions().stream());
    	
    	return Stream.concat(sectionQuestionsStream, childSectionsQuestionsStream).filter(q -> q.isType(type))
        .map(questionMapper::mapToResource)
        .collect(toList());
    }
    
    private ServiceResult<List<ValidationMessages>> setComplete(Long questionId, Long applicationId, Long processRoleId, boolean markAsComplete) {
        return find(processRole(processRoleId), openApplication(applicationId), getQuestionSupplier(questionId)).andOnSuccess((markedAsCompleteBy, application, question)
                -> setCompleteOnFindAndSuccess(markedAsCompleteBy, application, question, processRoleId, markAsComplete));
    }

    private ServiceResult<List<ValidationMessages>> setCompleteOnFindAndSuccess(ProcessRole markedAsCompleteBy, Application application, Question question, Long processRoleId, boolean markAsComplete){

        QuestionStatus questionStatus = null;

        if (question.hasMultipleStatuses()) {
            //INFUND-3016: The current user might not have a QuestionStatus, but maybe someone else in his organisation does? If so, use that one.
            List<ProcessRole> otherOrganisationMembers = processRoleRepository.findByApplicationIdAndOrganisationId(application.getId(), markedAsCompleteBy.getOrganisationId());
            Optional<QuestionStatus> optionalQuestionStatus = otherOrganisationMembers.stream()
                    .map(m -> getQuestionStatusByMarkedAsCompleteId(question, application.getId(), m.getId()))
                    .filter(m -> m != null)
                    .findFirst();
            questionStatus = optionalQuestionStatus.orElse(null);
        } else {
            questionStatus = getQuestionStatusByMarkedAsCompleteId(question, application.getId(), processRoleId);
        }

        List<ValidationMessages> validationMessages = markAsComplete ? validationUtil.isQuestionValid(question, application, markedAsCompleteBy.getId()): new ArrayList<>();

        if (questionStatus == null) {
            questionStatus = new QuestionStatus(question, application, markedAsCompleteBy, markAsComplete);
        } else if (markAsComplete) {
            questionStatus.markAsComplete();
        } else {
            questionStatus.markAsInComplete();
        }

        questionStatusRepository.save(questionStatus);
        BigDecimal completion = applicationService.getProgressPercentageBigDecimalByApplicationId(application.getId()).getSuccessObject();
        application.setCompletion(completion);
        applicationRepository.save(application);

        return serviceSuccess(validationMessages);
    }

    private Question getNextQuestionBySection(Long section, Long competitionId) {
        SectionResource nextSection = sectionService.getNextSection(section).getSuccessObjectOrNull();
        if (nextSection != null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(competitionId, nextSection.getId());
        }
        return null;
    }

    private Question getPreviousQuestionBySection(Long section, Long competitionId) {
        SectionResource previousSection = sectionService.getPreviousSection(section).getSuccessObjectOrNull();

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

    private List<QuestionStatus> filterByOrganisationIdIfHasMultipleStatuses(final List<QuestionStatus> questionStatuses, Long organisationId) {
        return questionStatuses.stream().
                filter(qs -> !qs.getQuestion().hasMultipleStatuses() || (qs.getAssignee() != null && qs.getAssignee().getOrganisationId().equals(organisationId)))
                .collect(Collectors.toList());
    }

    private Supplier<ServiceResult<Assessment>> getAssessment(Long assessmentId) {
        return () -> find(assessmentRepository.findOne(assessmentId), notFoundError(Assessment.class, assessmentId));
    }

    private Supplier<ServiceResult<Question>> getQuestionSupplier(Long questionId) {
        return () -> find(questionRepository.findOne(questionId), notFoundError(Question.class, questionId));
    }

    private ServiceResult<QuestionResource> getQuestionResource(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(QuestionResource.class, questionId)).andOnSuccessReturn(questionMapper::mapToResource);
    }

    private List<QuestionResource> questionsToResources(List<Question> filtered) {
        return simpleMap(filtered, question -> questionMapper.mapToResource(question));
    }
}
