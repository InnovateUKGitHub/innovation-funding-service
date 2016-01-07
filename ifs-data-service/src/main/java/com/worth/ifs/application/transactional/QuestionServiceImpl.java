package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class QuestionServiceImpl extends BaseTransactionalService implements QuestionService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    QuestionStatusRepository questionStatusRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    SectionService sectionService;


    @Override
    public Question getQuestionById(final Long id) {
        return questionRepository.findOne(id);
    }

    @Override
    public void markAsComplete(final Long questionId,
                               final Long applicationId,
                               final Long markedAsCompleteById){
        setComplete(questionId, applicationId, markedAsCompleteById, true);
    }

    @Override
    public void markAsInComplete(final Long questionId,
                                 final Long applicationId,
                                 final Long markedAsInCompleteById){
        setComplete(questionId, applicationId, markedAsInCompleteById, false);
    }

    private void setComplete(Long questionId, Long applicationId, Long processRoleId, boolean markAsComplete) {
        ProcessRole markedAsCompleteBy = processRoleRepository.findOne(processRoleId);
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        QuestionStatus questionStatus = getQuestionStatusByMarkedAsCompleteId(question, applicationId, processRoleId);
        if (questionStatus == null) {
            questionStatus = new QuestionStatus(question, application, markedAsCompleteBy, markAsComplete);
        } else if (markAsComplete) {
            questionStatus.markAsComplete();
        } else {
            questionStatus.markAsInComplete();
        }
        questionStatusRepository.save(questionStatus);
    }

    @Override
    public void assign(final Long questionId,
                       final Long applicationId,
                       final Long assigneeId,
                       final Long assignedById) {
        Question question = questionRepository.findOne(questionId);
        Application application = applicationRepository.findOne(applicationId);
        ProcessRole assignee = processRoleRepository.findOne(assigneeId);
        ProcessRole assignedBy = processRoleRepository.findOne(assignedById);

        QuestionStatus questionStatus = getQuestionStatusByApplicationIdAndAssigneeId(question, applicationId, assigneeId);

        if(questionStatus==null) {
            questionStatus = new QuestionStatus(question, application, assignee, assignedBy, now());
        } else {
            questionStatus.setAssignee(assignee, assignedBy, LocalDateTime.now());
        }
        questionStatusRepository.save(questionStatus);
    }

    @Override
    public Set<Long> getMarkedAsComplete(Long applicationId,
                                         Long organisationId) {
        Application application = applicationRepository.findOne(applicationId);
        List<Question> questions = questionRepository.findByCompetitionId(application.getCompetition().getId());
        return questions
                .stream()
                .filter(q -> q.isMarkAsCompletedEnabled() && questionStatusRepository.findByQuestionIdAndApplicationId(q.getId(), applicationId)
                        .stream()
                        .anyMatch(qs ->
                                (q.hasMultipleStatuses() && isMarkedAsCompleteForOrganisation(qs, applicationId, organisationId).orElse(Boolean.FALSE)) ||
                                        (!q.hasMultipleStatuses() && isMarkedAsCompleteForSingleStatus(qs).orElse(Boolean.FALSE))))
                .map(Question::getId).collect(Collectors.toSet());
    }


    private QuestionStatus getQuestionStatusByApplicationIdAndAssigneeId(Question question, Long applicationId, Long assigneeId) {
        if(question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(question.getId(), applicationId, assigneeId);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus getQuestionStatusByMarkedAsCompleteId(Question question, Long applicationId, Long markedAsCompleteById) {
        if(question.hasMultipleStatuses()) {
            return questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(question.getId(), applicationId, markedAsCompleteById);
        } else {
            return findByQuestionIdAndApplicationId(question.getId(), applicationId);
        }
    }

    private QuestionStatus findByQuestionIdAndApplicationId(Long questionId, Long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        if(questionStatuses!=null && !questionStatuses.isEmpty()) {
            return questionStatuses.get(0);
        }
        return null;
    }

    @Override
    public void updateNotification(final Long questionStatusId,
                                   final Boolean notify) {
        QuestionStatus questionStatus = questionStatusRepository.findOne(questionStatusId);
        questionStatus.setNotified(notify);
        questionStatusRepository.save(questionStatus);
    }

    @Override
    public List<Question> findByCompetition(final Long competitionId) {
        return questionRepository.findByCompetitionId(competitionId);
    }

    @Override
    public Question getNextQuestion(final Long questionId) {
        Question question = questionRepository.findOne(questionId);
        Question nextQuestion = null;
        if(question!=null) {
            // retrieve next question within current section
            nextQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

            // retrieve next question in following section
            if(nextQuestion == null) {
                nextQuestion = getNextQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
            }

            // retrieve next question in any other section, but with higher priority
            if(nextQuestion == null) {
                nextQuestion = questionRepository.findFirstByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(
                    question.getCompetition().getId(), question.getPriority());
            }
        }

        return nextQuestion;
    }

    private Question getNextQuestionBySection(Long section, Long competitionId) {
        Section nextSection = sectionService.getNextSection(section);
        if(nextSection!=null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(competitionId, nextSection.getId());
        }
        return null;

    }

    @Override
    public Question getPreviousQuestionBySection(final Long sectionId) {
        Section section = sectionService.getById(sectionId);
        if(section!=null && section.getParentSection()!=null) {
            Section previousSection = sectionService.getPreviousSection(section);
            if(previousSection!=null) {
                Optional<Question> lastQuestionInSection = previousSection.getQuestions()
                        .stream()
                        .max(comparing(Question::getPriority));
                return lastQuestionInSection.orElse(null);
            }
        }
        return null;
    }

    @Override
    public Question getNextQuestionBySection(final Long sectionId) {
        Section section = sectionService.getById(sectionId);
        if(section!=null && section.getParentSection()!=null) {
            Section nextSection = sectionService.getNextSection(section);
            if(nextSection!=null) {
                Optional<Question> firstQuestionInSection = nextSection.getQuestions()
                        .stream()
                        .min(comparing(question -> question.getPriority()));
                return firstQuestionInSection.orElse(null);
            }
        }
        return null;
    }

    @Override
    public Question getPreviousQuestion(final Long questionId) {
        Question question = questionRepository.findOne(questionId);
        Question previousQuestion = null;
        if(question!=null) {
            previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

            if(previousQuestion==null) {
                previousQuestion = getPreviousQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
            }
        }

        return previousQuestion;
    }

    private Question getPreviousQuestionBySection(Long section, Long competitionId) {
        Section previousSection = sectionService.getPreviousSection(section);

        if(previousSection!=null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(competitionId, previousSection.getId());
        }
        return null;
    }

    @Override
    public Boolean isMarkedAsComplete(Question question, Long applicationId, Long organisationId) {
        if(question.hasMultipleStatuses()) {
            return isMarkedAsCompleteForOrganisation(question.getId(), applicationId, organisationId);
        } else {
            return isMarkedAsCompleteForSingleStatus(question.getId(), applicationId);
        }
    }

    private Boolean isMarkedAsCompleteForOrganisation(Long questionId, Long applicationId, Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        for(QuestionStatus questionStatus : questionStatuses) {
            Optional<Boolean> markedAsComplete = isMarkedAsCompleteForOrganisation(questionStatus, applicationId, organisationId);
            if(markedAsComplete.isPresent()) {
                return markedAsComplete.get();
            }
        }
        return Boolean.FALSE;
    }

    private Boolean isMarkedAsCompleteForSingleStatus(Long questionId, Long applicationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        if(questionStatuses!=null && !questionStatuses.isEmpty()) {
            return isMarkedAsCompleteForSingleStatus(questionStatuses.get(0)).orElse(Boolean.FALSE);
        }
        return Boolean.FALSE;
    }


    private Optional<Boolean> isMarkedAsCompleteForOrganisation(QuestionStatus questionStatus, Long applicationId, Long organisationId) {
        Boolean markedAsComplete = null;
        if (questionStatus.getMarkedAsCompleteBy() != null &&
                questionStatus.getMarkedAsCompleteBy().getOrganisation().getId().equals(organisationId)) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }

    private Optional<Boolean> isMarkedAsCompleteForSingleStatus(QuestionStatus questionStatus) {
        Boolean markedAsComplete = null;
        if(questionStatus!=null) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }
}
