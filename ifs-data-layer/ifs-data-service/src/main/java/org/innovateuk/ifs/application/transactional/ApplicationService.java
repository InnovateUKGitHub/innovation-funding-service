package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Security annotated interface for {@ApplicationServiceImpl}.
 */
public interface ApplicationService {

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'CREATE')")
    ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(final String applicationName, @P("competitionId") final Long competitionId, final Long userId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> saveApplicationDetails(@P("applicationId") final Long id, ApplicationResource application);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(@P("applicationId") final Long id, ZonedDateTime date);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "SET_FUNDING_DECISION_EMAIL_DATE", securedType = ApplicationResource.class, description = "Comp Admins should be able to set the funding decision email date")
    ServiceResult<ApplicationResource> setApplicationFundingEmailDateTime(@P("applicationId") final Long applicationId, ZonedDateTime fundingEmailDate);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_APPLICATION_STATE')")
    ServiceResult<ApplicationResource> updateApplicationState(@P("applicationId") final Long id, final ApplicationState state);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_AS_INELIGIBLE')")
    ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcome reason);


    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value="WITHDRAW", description = "Only the IFS administrators have permission to withdraw an application")
    ServiceResult<Void> withdrawApplication(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> showApplicationTeam(final Long applicationId, final Long userId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationResource> getApplicationById(@P("applicationId") final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<CompletedPercentageResource> getProgressPercentageByApplicationId(@P("applicationId") final Long applicationId);

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

    @SecuredBySpring(value = "READ", description = "Support or IFS Admin can search applications from competition dashboard")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    ServiceResult<ApplicationPageResource> wildcardSearchById(String searchString, Pageable pageable);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<ZonedDateTime> findLatestEmailFundingDateByCompetitionId(Long id);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationResource> findByProcessRole(Long id);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_UNSUCCESSFUL_APPLICATIONS')")
    ServiceResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageIndex, int pageSize, String sortField);

}
