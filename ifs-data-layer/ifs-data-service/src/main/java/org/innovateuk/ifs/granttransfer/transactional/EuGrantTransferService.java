package org.innovateuk.ifs.granttransfer.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

public interface EuGrantTransferService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> uploadGrantAgreement(String contentType, String contentLength, String originalFilename, long applicationId,
                                       HttpServletRequest request);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> deleteGrantAgreement(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'applicant')")
    @SecuredBySpring(value = "DOWNLOAD_GRANT_AGREEMENT",
            description = "Competition Admins, Project Finance users and applicants can download grant agreement")
    ServiceResult<FileAndContents> downloadGrantAgreement(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'applicant')")
    @SecuredBySpring(value = "FIND_GRANT_AGREEMENT",
            description = "Competition Admins, Project Finance users and applicants can download grant agreement")
    ServiceResult<FileEntryResource> findGrantAgreement(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'applicant')")
    @SecuredBySpring(value = "GET_GRANT_TRANSFER_DETAILS",
            description = "Competition Admins, Project Finance users and applicants can view grant transfer details")
    ServiceResult<EuGrantTransferResource> getGrantTransferByApplicationId(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> updateGrantTransferByApplicationId(EuGrantTransferResource euGrantTransferResource, long applicationId);
}
