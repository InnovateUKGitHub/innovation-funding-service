package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link Application} related data.
 */
public interface ApplicationService {
    public Application getById(Long applicationId);
    public List<Application> getInProgress(Long userId);
    public List<Application> getFinished(Long userId);
    public void updateStatus(Long applicationId, Long statusId);
    public int getCompleteQuestionsPercentage(Long applicationId);
    public void save(Application application);
    Map<Long, Integer> getProgress(Long userId);
}
