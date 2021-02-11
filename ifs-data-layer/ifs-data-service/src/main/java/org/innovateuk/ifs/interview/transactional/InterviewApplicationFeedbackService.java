package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

/**
 * Service for managing uploading feedback for an {@link org.innovateuk.ifs.interview.domain.InterviewAssignment}
 */
public interface InterviewApplicationFeedbackService {

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "UPLOAD_FEEDBACK",
            description = "Competition Admins and Project Finance users can upload feedback")
    ServiceResult<Void> uploadFeedback(String contentType, String contentLength, String originalFilename, long applicationId,
                                       HttpServletRequest request);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "DELETE_FEEDBACK",
            description = "Competition Admins and Project Finance users can delete feedback")
    ServiceResult<Void> deleteFeedback(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'applicant', 'assessor', 'monitoring_officer')")
    @SecuredBySpring(value = "DOWNLOAD_FEEDBACK",
            description = "Competition Admins, Project Finance users, applicants and assessors can download feedback")
    ServiceResult<FileAndContents> downloadFeedback(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'applicant', 'assessor', 'monitoring_officer')")
    @SecuredBySpring(value = "FIND_FEEDBACK",
            description = "Competition Admins, Project Finance users, applicants and assessors can find feedback")
    ServiceResult<FileEntryResource> findFeedback(long applicationId);
}