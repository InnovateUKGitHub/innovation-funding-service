package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
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
    RestResult<ValidationMessages> saveApplication(ApplicationResource application);
    RestResult<ApplicationResource> createApplication(long competitionId, long userId, long organisationId, String applicationName);
    RestResult<Void> updateApplicationState(long applicationId, ApplicationState state);
    Future<RestResult<Double>> getCompleteQuestionsPercentage(Long applicationId);
    RestResult<Integer> getAssignedQuestionsCount(Long applicationId, Long assigneeId);
    RestResult<ApplicationResource> findByProcessRoleId(Long id);
    RestResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason);
    RestResult<Void> informIneligible(long applicationId, ApplicationIneligibleSendResource applicationIneligibleSendResource);
    RestResult<Boolean> showApplicationTeam(Long applicationId, Long userId);
    RestResult<ZonedDateTime> getLatestEmailFundingDate(Long competitionId);
    RestResult<Void> hideApplication(long applicationId, long userId);
    RestResult<Void> deleteApplication(long applicationId);
    RestResult<Void> reopenApplication(long applicationId);
}
