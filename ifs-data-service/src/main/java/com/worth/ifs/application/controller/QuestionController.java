package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    QuestionStatusRepository questionStatusRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    SectionController sectionController;


    @RequestMapping("/id/{id}")
    public Question getQuestionById(@PathVariable("id") final Long id) {
        return questionRepository.findOne(id);
    }

    @RequestMapping(value="/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public void markAsComplete(@PathVariable("questionId") final Long questionId,
                               @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsCompleteById") final Long markedAsCompleteById){
        setComplete(questionId, applicationId, markedAsCompleteById, true);
    }

    @RequestMapping(value="/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public void markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById){
        setComplete(questionId, applicationId, markedAsInCompleteById, false);
    }

    private void setComplete(Long questionId, Long applicationId, Long markedById, boolean markAsComplete) {
        ProcessRole markedAsCompleteBy = processRoleRepository.findOne(markedById);
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        QuestionStatus questionStatus = getQuestionStatusByMarkedAsCompleteId(question, applicationId, markedById);
        if (questionStatus == null) {
            questionStatus = new QuestionStatus(question, application, markedAsCompleteBy, markAsComplete);
        } else if (markAsComplete) {
            questionStatus.markAsComplete();
        } else {
            questionStatus.markAsInComplete();
        }
        questionStatusRepository.save(questionStatus);
    }

    @RequestMapping(value="/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public void assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        Question question = questionRepository.findOne(questionId);
        Application application = applicationRepository.findOne(applicationId);
        ProcessRole assignee = processRoleRepository.findOne(assigneeId);
        ProcessRole assignedBy = processRoleRepository.findOne(assignedById);

        QuestionStatus questionStatus = getQuestionStatusByApplicationIdAndAssigneeId(question, applicationId, assigneeId);

        if(questionStatus==null) {
            questionStatus = new QuestionStatus(question, application, assignee, assignedBy, LocalDateTime.now());
        } else {
            questionStatus.setAssignee(assignee, assignedBy, LocalDateTime.now());
        }
        questionStatusRepository.save(questionStatus);
    }

    @RequestMapping(value="/getMarkedAsComplete/{applicationId}/{organisationId}")
    public Set<Long> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
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

    @RequestMapping(value="/updateNotification/{questionStatusId}/{notify}")
    public void updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                       @PathVariable("notify") final Boolean notify) {
        QuestionStatus questionStatus = questionStatusRepository.findOne(questionStatusId);
        questionStatus.setNotified(notify);
        questionStatusRepository.save(questionStatus);
    }

    @RequestMapping(value="/findByCompetition/{competitionId}")
    public List<Question> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionRepository.findByCompetitionId(competitionId);
    }

    @RequestMapping(value="/getNextQuestion/{questionId}")
    public Question getNextQuestion(@PathVariable("questionId") final Long questionId) {
        Question question = questionRepository.findOne(questionId);
        Question nextQuestion = null;
        if(question!=null) {
            nextQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                    question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

            if(nextQuestion==null) {
                Section nextSection = sectionController.getNextSection(question.getSection());
                if (nextSection != null) {
                    nextQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(question.getCompetition().getId(), nextSection.getId());
                }
            }
        }

        return nextQuestion;
    }

    @RequestMapping(value="/getPreviousQuestion/{questionId}")
    public Question getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        Question question = questionRepository.findOne(questionId);
        Question previousQuestion = null;
        if(question!=null) {
            previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                    question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

            if(previousQuestion==null) {
                Section previousSection = sectionController.getPreviousSection(question.getSection());
                if(previousSection!=null) {
                    previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(question.getCompetition().getId(), previousSection.getId());
                }
            }
        }

        return previousQuestion;
    }

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
