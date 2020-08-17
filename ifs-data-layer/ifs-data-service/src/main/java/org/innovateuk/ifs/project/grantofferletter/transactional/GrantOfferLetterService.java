package org.innovateuk.ifs.project.grantofferletter.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.string.resource.StringResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Project processing work - grant offer service.
 **/
public interface GrantOfferLetterService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_GRANT_OFFER')")
    ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_GRANT_OFFER')")
    ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_GRANT_OFFER')")
    ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_GRANT_OFFER')")
    ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_GRANT_OFFER')")
    ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_GRANT_OFFER')")
    ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_SIGNED_GRANT_OFFER')")
    ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin and project finance user are able to create a grant offer letter" )
    ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "DELETE", securedType = ProjectResource.class, description = "Only comp admin and project finance user are able to delete a grant offer letter" )
    ServiceResult<Void> removeGrantOfferLetterFileEntry(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DELETE_SIGNED_GRANT_OFFER')")
    ServiceResult<Void> removeSignedGrantOfferLetterFileEntry(Long projectId);

    @PreAuthorize("hasAnyAuthority('system_maintainer' , 'project_finance')")
    @SecuredBySpring(value = "DELETE", securedType = ProjectResource.class, description = "Only project finance user or system maintenance user are able to delete a GOL to reset finance checks" )
    @Activity(projectId = "projectId", type = ActivityType.GRANT_OFFER_LETTER_DELETED)
    ServiceResult<Void> deleteGrantOfferLetterFileEntry(Long projectId);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin and project finance user are able to create a additional contract for Appendix 2 if any")
    ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_SIGNED_GRANT_OFFER')")
    @Activity(projectId = "projectId", type = ActivityType.GRANT_OFFER_LETTER_UPLOADED)
    ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId,'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SUBMIT_GRANT_OFFER_LETTER')")
    @Activity(projectId = "projectId", type = ActivityType.GRANT_OFFER_LETTER_SIGNED)
    ServiceResult<Void> submitGrantOfferLetter(@P("projectId")Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'SEND_GRANT_OFFER_LETTER')")
    @Activity(projectId = "projectId", type = ActivityType.GRANT_OFFER_LETTER_PUBLISHED)
    ServiceResult<Void> sendGrantOfferLetter(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'APPROVE_SIGNED_GRANT_OFFER_LETTER')")
    @Activity(projectId = "projectId", dynamicType = "approveOrRejectActivityType")
    ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> approveOrRejectActivityType(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
        return grantOfferLetterApprovalResource.getApprovalType() == ApprovalType.APPROVED ?
                Optional.of(ActivityType.GRANT_OFFER_LETTER_APPROVED) : Optional.of(ActivityType.GRANT_OFFER_LETTER_REJECTED);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_GRANT_OFFER_LETTER_SEND_STATUS')")
    ServiceResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId);


    @PreAuthorize("hasPermission(#projectId,'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SUBMIT_GRANT_OFFER_LETTER')")
    ServiceResult<StringResource> getDocusignUrl(long projectId);

    @PreAuthorize("hasPermission(#projectId,'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SUBMIT_GRANT_OFFER_LETTER')")
    @SecuredBySpring(value = "IMPORT_DOCUMENT", description = "Applicants can request their signed documents" )
    @Activity(projectId = "projectId", type = ActivityType.GRANT_OFFER_LETTER_SIGNED)
    ServiceResult<Void> importGrantOfferLetter(long projectId);

}
