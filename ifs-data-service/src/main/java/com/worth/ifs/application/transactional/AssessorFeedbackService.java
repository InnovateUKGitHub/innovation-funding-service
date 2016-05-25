package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.NotSecured;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

public interface AssessorFeedbackService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(@P("applicationId") long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<Pair<FileEntryResource, Supplier<InputStream>>> getAssessorFeedbackFileEntryContents(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> getAssessorFeedbackFileEntryDetails(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'REMOVE_ASSESSOR_FEEDBACK')")
    ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId);

    @PreAuthorize("hasPermission(#competitionId, 'com.worth.ifs.application.resource.CompetitionResource', 'CHECK_ASSESSOR_FEEDBACK_UPLOADED')")
	ServiceResult<Boolean> assessorFeedbackUploaded(long competitionId);

    @PreAuthorize("hasPermission(#competitionId, 'com.worth.ifs.application.resource.CompetitionResource', 'SUBMIT_ASSESSOR_FEEDBACK')")
	ServiceResult<Void> submitAssessorFeedback(long competitionId);
}