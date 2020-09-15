package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

/**
 * Security annotated interface for {@ApplicationServiceImpl}.
 */
public interface ApplicationService {

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'CREATE')")
    ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(final String applicationName, final long competitionId, final long userId, long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ValidationMessages> saveApplicationDetails(Long applicationId, ApplicationResource application);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(Long applicationId, ZonedDateTime date);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "SET_FUNDING_DECISION_EMAIL_DATE", securedType = ApplicationResource.class, description = "Comp Admins should be able to set the funding decision email date")
    ServiceResult<ApplicationResource> setApplicationFundingEmailDateTime(Long applicationId, ZonedDateTime fundingEmailDate);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_APPLICATION_STATE')")
    @Activity(dynamicType = "submittedActivityType", applicationId = "applicationId")
    ServiceResult<ApplicationResource> updateApplicationState(long applicationId, ApplicationState state);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> submittedActivityType(long applicationId, ApplicationState state) {
        if (SUBMITTED == state) {
            return Optional.of(ActivityType.APPLICATION_SUBMITTED);
        }
        return Optional.empty();
    }

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'REOPEN_APPLICATION')")
    @Activity(type = ActivityType.APPLICATION_REOPENED, applicationId = "applicationId")
    ServiceResult<Void> reopenApplication(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_AS_INELIGIBLE')")
    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcome reason);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> showApplicationTeam(final Long applicationId, final Long userId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationResource> getApplicationById(Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                                     final Long userId,
                                                                                     final Role role);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the applications")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<List<Application>> getApplicationsByCompetitionIdAndState(Long competitionId, Collection<ApplicationState> applicationStates);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> findByUserId(final Long userId);

    @SecuredBySpring(value = "READ", description = "Only internal users can search applications from competition dashboard")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'ifs_administrator')")
    ServiceResult<ApplicationPageResource> wildcardSearchById(String searchString, Pageable pageable);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<ZonedDateTime> findLatestEmailFundingDateByCompetitionId(Long id);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationResource> findByProcessRole(Long id);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<CompetitionResource> getCompetitionByApplicationId(long applicationId);

    @NotSecured(value = "Only called by other secured services")
    ServiceResult<Void> linkAddressesToOrganisation(long organisationId, long applicationId);
}
