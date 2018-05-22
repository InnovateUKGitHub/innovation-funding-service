package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ApplicationRestService {
    RestResult<ApplicationResource> getApplicationById(Long applicationId);
    RestResult<List<ApplicationResource>> getApplicationsByUserId(Long userId);
    RestResult<ApplicationPageResource> wildcardSearchById(String searchString, int pageNumber, int pageSize);
    RestResult<Boolean> isApplicationReadyForSubmit(Long applicationId);
    RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userId, Role role);
    RestResult<Void> saveApplication(ApplicationResource application);
    RestResult<ApplicationResource> createApplication(Long competitionId, Long userId, String applicationName);
    RestResult<Void> updateApplicationState(Long applicationId, ApplicationState state);
    Future<RestResult<Double>> getCompleteQuestionsPercentage(Long applicationId);
    RestResult<Integer> getAssignedQuestionsCount(Long applicationId, Long assigneeId);
    RestResult<ApplicationResource> findByProcessRoleId(Long id);
    RestResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);
    RestResult<Void> informIneligible(long applicationId, ApplicationIneligibleSendResource applicationIneligibleSendResource);
    RestResult<Void> withdrawApplication(long applicationId);
    RestResult<Boolean> showApplicationTeam(Long applicationId, Long userId);
    RestResult<ZonedDateTime> getLatestEmailFundingDate(Long competitionId);
    RestResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageNumber, int pageSize, String sortField);
}
