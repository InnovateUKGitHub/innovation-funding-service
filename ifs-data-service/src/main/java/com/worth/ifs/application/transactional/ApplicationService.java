package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Application processing work
 */
public interface ApplicationService {

    @PreAuthorize("hasAuthority('applicant')")
    Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, final Long competitionId, final Long userId);

    @PreAuthorize("hasPermission(#fileEntry, 'UPDATE')")
    Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#fileEntry, 'UPDATE')")
    Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> updateFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#fileEntry, 'com.worth.ifs.application.resource.FormInputResponseFileEntryResource', 'UPDATE')")
    Either<ServiceFailure, FormInputResponse> deleteFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntryId);

    @PreAuthorize("hasPermission(#fileEntry, 'com.worth.ifs.application.resource.FormInputResponseFileEntryResource', 'READ')")
    Either<ServiceFailure, Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> getFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntryId);

    @NotSecured("TODO")
    ApplicationResourceHateoas getApplicationByIdHateoas(final Long id);

    @NotSecured("TODO")
    Resources<ApplicationResourceHateoas> findAllHateoas();

    @NotSecured("TODO")
    ApplicationResource getApplicationById(final Long id);

    @NotSecured("TODO")
    List<ApplicationResource> findAll();

    @NotSecured("TODO")
    List<ApplicationResource> findByUserId(final Long userId);

    /**
     * This method saves only a few application attributes that
     * the user is able to modify on the application form.
     */
    @NotSecured("TODO")
    ResponseEntity<String> saveApplicationDetails(final Long id, ApplicationResource application);

    @NotSecured("TODO")
    ObjectNode getProgressPercentageByApplicationId(final Long applicationId);

    @NotSecured("TODO")
    ResponseEntity<String> updateApplicationStatus(final Long id,
                                                   final Long statusId);

    @NotSecured("TODO")
    List<ApplicationResource> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                      final Long userId,
                                                                      final UserRoleType role);

    @NotSecured("TODO")
    ApplicationResource createApplicationByApplicationNameForUserIdAndCompetitionId(
            final Long competitionId,
            final Long userId,
            JsonNode jsonObj);


}
