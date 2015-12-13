package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link Question} related data,
 * through the RestService {@link QuestionRestService}.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    QuestionRestService questionRestService;

    @Override
    public void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        questionRestService.assign(questionId, applicationId, assigneeId, assignedById);
    }

    @Override
    public void markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        questionRestService.markAsComplete(questionId, applicationId, markedAsCompleteById);
        questionRestService.assign(questionId, applicationId, 0L, 0L);
    }

    @Override
    public void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        questionRestService.markAsInComplete(questionId, applicationId, markedAsInCompleteById);
    }

    @Override
    public List<Question> findByCompetition(Long competitionId) {
        return questionRestService.findByCompetition(competitionId);
    }

    @Override
    public HashMap<Long, QuestionStatus> mapAssigneeToQuestion(List<Question> questions, Long userOrganisationId) {
        HashMap<Long, QuestionStatus> questionAssignees = new HashMap<>();
        for(Question question : questions) {
            for(QuestionStatus questionStatus : question.getQuestionStatuses()) {
                if(questionStatus.getAssignee()==null)
                    continue;
                boolean multipleStatuses = question.hasMultipleStatuses();
                boolean assigneeIsPartOfOrganisation = questionStatus.getAssignee().getOrganisation().getId().equals(userOrganisationId);

                if((multipleStatuses && assigneeIsPartOfOrganisation) || !multipleStatuses) {
                    questionAssignees.put(question.getId(), questionStatus);
                    break;
                }
            }
        }
        return questionAssignees;
    }

    @Override
    public List<QuestionStatus> getNotificationsForUser(Collection<QuestionStatus> questionStatuses, Long userId) {
        return questionStatuses.stream().filter(qs -> qs.getAssignee().getUser().getId().equals(userId) && (qs.getNotified()!=null && qs.getNotified().equals(Boolean.FALSE))).collect(Collectors.toList());
    }

    @Override
    public void removeNotifications(List<QuestionStatus> questionStatuses) {
        questionStatuses.stream().forEach(qs -> questionRestService.updateNotification(qs.getId(), true));
    }

    public Set<Long> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return questionRestService.getMarkedAsComplete(applicationId, organisationId);
    }

    @Override
    public Question findById(Long questionId) {
        return questionRestService.findById(questionId);
    }

    @Override
    public Question findNextQuestion(Long questionId) {
        return questionRestService.getNextQuestion(questionId);
    }

    @Override
    public Question findPreviousQuestion(Long questionId) {
        return questionRestService.getPreviousQuestion(questionId);
    }
}
