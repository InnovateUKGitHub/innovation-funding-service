package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface QuestionService {
    public void assign(Long questionId, Long assigneeId, Long assignedById);
    public void markAsComplete(Long questionId, Long markedAsCompleteById);
    public void markAsInComplete(Long questionId, Long markedAsInCompleteById);
    public List<Question> findByCompetition(Long competitionId);
    public HashMap<Long, QuestionStatus> mapAssigneeToQuestion(List<Question> questions, Long userOrganisationId);
    public List<QuestionStatus> getNotificationsForUser(Collection<QuestionStatus> questionStatuses, Long userId);
    public void removeNotifications(List<QuestionStatus> questionStatuses);
}
