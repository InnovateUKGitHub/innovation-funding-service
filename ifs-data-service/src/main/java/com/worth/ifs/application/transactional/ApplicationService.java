package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.commons.security.SecuredBySpring;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Transactional and secure service for com.worth.ifs.Application processing work
 */
public interface ApplicationService {

    @PreAuthorize("hasAuthority('applicant') || hasAnyAuthority('applicant', 'system_registrar')")
    @SecuredBySpring(value = "CREATE",
            description = "Any logged in user with Global roles or user with system registrar role can create and application",
            securedType = ApplicationResource.class)
    ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, final Long competitionId, final Long userId);

    @PreAuthorize("hasPermission(#fileEntry, 'UPDATE')")
    ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#fileEntry, 'UPDATE')")
    ServiceResult<Void> updateFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#fileEntry, 'com.worth.ifs.application.resource.FormInputResponseFileEntryResource', 'UPDATE')")
    ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntry);

    @PreAuthorize("hasPermission(#fileEntry, 'com.worth.ifs.application.resource.FormInputResponseFileEntryResource', 'READ')")
    ServiceResult<FormInputResponseFileAndContents> getFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntry);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationResource> getApplicationById(@P("applicationId") final Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> findByUserId(final Long userId);

    /**
     * This method saves only a few application attributes that
     * the user is able to modify on the application form.
     */
    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> saveApplicationDetails(@P("applicationId") final Long id, ApplicationResource application);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(@P("applicationId") final Long id, LocalDateTime date);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<CompletedPercentageResource> getProgressPercentageByApplicationId(@P("applicationId") final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<ApplicationResource> updateApplicationStatus(@P("applicationId") final Long id,
                                                               final Long statusId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'APPLICATION_SUBMITTED_NOTIFICATION')")
    ServiceResult<Void> sendNotificationApplicationSubmitted(@P("applicationId") Long application);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                                     final Long userId,
                                                                                     final UserRoleType role);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationResource> findByProcessRole(Long id);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ObjectNode> applicationReadyForSubmit(final Long id);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
	ServiceResult<List<Application>> getApplicationsByCompetitionIdAndStatus(Long competitionId, Collection<Long> applicationStatusId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<BigDecimal> getProgressPercentageBigDecimalByApplicationId(Long applicationId);
}
