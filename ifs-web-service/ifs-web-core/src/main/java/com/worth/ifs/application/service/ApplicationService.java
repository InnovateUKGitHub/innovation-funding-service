package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationService {
    ApplicationResource getById(Long applicationId);
    List<ApplicationResource> getInProgress(Long userId);
    List<ApplicationResource> getFinished(Long userId);
    Boolean isApplicationReadyForSubmit(Long applicationId);
    void updateStatus(Long applicationId, Long statusId);
    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    Integer getCompleteQuestionsPercentage(Long applicationId);
    void save(ApplicationResource application);
    Map<Long, Integer> getProgress(Long userId);
    int getAssignedQuestionsCount(Long applicantId, Long processRoleId);
    RestResult<ApplicationResource> findByProcessRoleId(Long id);
    OrganisationResource getLeadOrganisation(Long applicationId);
    void removeCollaborator(Long applicationInviteId);
}
