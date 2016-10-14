package com.worth.ifs.application.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

public interface AssessorFeedbackService {

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(@P("applicationId") long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileAndContents> getAssessorFeedbackFileEntryContents(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> getAssessorFeedbackFileEntryDetails(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<Void> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'REMOVE_ASSESSOR_FEEDBACK')")
    ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can find out if any submitted applications for a competition need feedback uploaded", securedType = CompetitionResource.class)
	ServiceResult<Boolean> assessorFeedbackUploaded(long competitionId);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", description = "Comp Admins can submit assessor feedback for a competition", securedType = CompetitionResource.class)
	ServiceResult<Void> submitAssessorFeedback(long competitionId);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", description = "Comp Admins can send out emails to Lead Applicants notifying them of the Assessor Feedback on their Applications", securedType = CompetitionResource.class)
    ServiceResult<Void> notifyLeadApplicantsOfAssessorFeedback(long competitionId);
}