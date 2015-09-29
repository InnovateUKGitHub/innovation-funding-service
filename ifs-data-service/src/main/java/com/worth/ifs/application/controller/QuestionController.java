package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QuestionController exposes question data through a REST API.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    QuestionStatusRepository questionStatusRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    /**
     * Mark a question as complete
     * @param questionId question which has been completed / incompleted
     * @param markedAsCompleteById processRoleId which represents the user role combination
     */
    @RequestMapping(value="/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public void markAsComplete(@PathVariable("questionId") final Long questionId,
                               @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsCompleteById") final Long markedAsCompleteById){
        setComplete(questionId, applicationId, markedAsCompleteById, true);
    }

    /**
     * Mark a question as incomplete
     * @param questionId question which has been completed / incompleted
     * @param markedAsInCompleteById processRoleId which represents the user role combination
     */
    @RequestMapping(value="/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public void markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById){
        setComplete(questionId, applicationId, markedAsInCompleteById, false);
    }

    private void setComplete(Long questionId, Long applicationId, Long markedById, boolean markedAsComplete) {
        ProcessRole markedAsCompleteBy = processRoleRepository.findOne(markedById);
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        QuestionStatus questionStatus = getQuestionStatusByMarkedAsCompleteId(question, applicationId, markedById);
        if (questionStatus == null) {
            questionStatus = new QuestionStatus(question, application, markedAsCompleteBy, markedAsComplete);
        } else if (markedAsComplete) {
            questionStatus.markAsComplete();
        } else {
            questionStatus.markAsInComplete();
        }
        questionStatusRepository.save(questionStatus);
    }

    /**
     * Assign a question to one of the collaborators or lead applicant
     * @param questionId question to which the assignee is assigned to
     * @param assigneeId processRoleId which represent the user role combination
     */
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
        if(questionStatuses!=null && questionStatuses.size()>0) {
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

    /**
     * Retrieve questions for the competition
     * @param competitionId the competition id
     * @return list of questions belonging to the competition
     */
    @RequestMapping(value="/findByCompetition/{competitionId}")
    public List<Question> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionRepository.findByCompetitionId(competitionId);
    }
}
