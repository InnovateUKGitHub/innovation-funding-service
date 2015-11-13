package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link Question} related data.
 */
public interface QuestionRestService {
    public void markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    public void markAsInComplete(Long questionId, Long applicationId,  Long markedAsInCompleteById);
    public void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    public List<Question> findByCompetition(Long competitionId);
    public void updateNotification(Long questionStatusId, Boolean notify);
    public Set<Long> getMarkedAsComplete(Long applicationId, Long organisationId);
    public Question findById(Long questionId);
}
