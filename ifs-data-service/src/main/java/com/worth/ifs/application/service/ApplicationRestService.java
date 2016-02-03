package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.UserRoleType;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Application} related data.
 */
public interface ApplicationRestService{
    ApplicationResource getApplicationById(Long applicationId);
    List<ApplicationResource> getApplicationsByUserId(Long userId);
    Boolean isApplicationReadyForSubmit(Long applicationId);
    List<ApplicationResource> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userId, UserRoleType role);
    void saveApplication(ApplicationResource application);
    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    void updateApplicationStatus(Long applicationId, Long statusId);
    ListenableFuture<Double> getCompleteQuestionsPercentage(Long applicationId);
    Integer getAssignedQuestionsCount(Long applicationId, Long assigneeId);
    ApplicationResource findByProcessRoleId(Long id);
}
