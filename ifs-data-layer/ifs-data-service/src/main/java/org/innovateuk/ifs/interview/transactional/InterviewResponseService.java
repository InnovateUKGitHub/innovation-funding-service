package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

/**
 * Service for responding to interview feedback.
 */
public interface InterviewResponseService {

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPLOAD_RESPONSE",
            description = "Applicant users can upload a response")
    ServiceResult<Void> uploadResponse(String contentType, String contentLength, String originalFilename, long applicationId,
                                       HttpServletRequest request);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "DELETE_RESPONSE",
            description = "Applicant users can delete a response")
    ServiceResult<Void> deleteResponse(long applicationId);

    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "DOWNLOAD_RESPONSE",
            description = "Applicant users, assessors and comp exec users can download a response")
    ServiceResult<FileAndContents> downloadResponse(long applicationId);

    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "FIND_RESPONSE",
            description = "Applicant users, assessors and comp exec users can find a response")
    ServiceResult<FileEntryResource> findResponse(long applicationId);

}