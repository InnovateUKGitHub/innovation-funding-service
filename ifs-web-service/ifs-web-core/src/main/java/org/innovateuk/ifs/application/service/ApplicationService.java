package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;

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
    ServiceResult<Void> updateState(Long applicationId, ApplicationState state);
    ApplicationResource createApplication(Long competitionId, Long userId, String applicationName);
    Integer getCompleteQuestionsPercentage(Long applicationId);
    ServiceResult<Void> save(ApplicationResource application);
    Map<Long, Integer> getProgress(Long userId);
    int getAssignedQuestionsCount(Long applicantId, Long processRoleId);
    ServiceResult<ApplicationResource> findByProcessRoleId(Long id);
    OrganisationResource getLeadOrganisation(Long applicationId);
    ServiceResult<Void> removeCollaborator(Long applicationInviteId);
}
