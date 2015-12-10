package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.UserRoleType;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Application} related data.
 */
public interface ApplicationRestService {
    public ApplicationResource getApplicationById(Long applicationId);
    public ApplicationResource getApplicationByIdHateoas(Long applicationId);
    public List<ApplicationResource> getApplicationsByUserId(Long userId);
    public List<ApplicationResource> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userId, UserRoleType role);
    public void saveApplication(ApplicationResource application);
    public ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    public void updateApplicationStatus(Long applicationId, Long statusId);
    public Double getCompleteQuestionsPercentage(Long applicationId);

    public Integer getAssignedQuestionsCount(Long applicationId, Long assigneeId);
}
