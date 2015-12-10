package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationService {
    public ApplicationResource getById(Long applicationId, Boolean... hateoas);
    public List<ApplicationResource> getInProgress(Long userId);
    public List<ApplicationResource> getFinished(Long userId);
    public void updateStatus(Long applicationId, Long statusId);
    public ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    public int getCompleteQuestionsPercentage(Long applicationId);
    public void save(ApplicationResource application);
    Map<Long, Integer> getProgress(Long userId);
    public int getAssignedQuestionsCount(Long applicantId, Long processRoleId);
}
