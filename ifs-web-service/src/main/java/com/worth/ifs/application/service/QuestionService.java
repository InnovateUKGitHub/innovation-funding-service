package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface QuestionService {
    public void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    public void markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    public void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById);
    public List<Question> findByCompetition(Long competitionId);
    public HashMap<Long, QuestionStatus> mapAssigneeToQuestion(List<Question> questions, Long userOrganisationId);
    public List<QuestionStatus> getNotificationsForUser(Collection<QuestionStatus> questionStatuses, Long userId);
    public void removeNotifications(List<QuestionStatus> questionStatuses);
    public Set<Long> getMarkedAsComplete(Long applicationId, Long organisationId);
}
