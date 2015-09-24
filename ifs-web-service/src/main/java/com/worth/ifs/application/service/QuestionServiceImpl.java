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
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    QuestionRestService questionRestService;

    @Override
    public void assign(Long questionId, Long assigneeId, Long assignedById) {
        questionRestService.assign(questionId, assigneeId, assignedById);
    }

    @Override
    public void markAsComplete(Long questionId, Long markedAsCompleteById) {
        questionRestService.markAsComplete(questionId, markedAsCompleteById);
        questionRestService.assign(questionId, 0L, 0L);
    }

    @Override
    public void markAsInComplete(Long questionId, Long markedAsInCompleteById) {
        questionRestService.markAsInComplete(questionId, markedAsInCompleteById);
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

}
