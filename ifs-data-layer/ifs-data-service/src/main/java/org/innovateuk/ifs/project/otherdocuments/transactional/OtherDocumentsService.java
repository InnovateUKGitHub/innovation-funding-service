package org.innovateuk.ifs.project.otherdocuments.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Project Other Documents processing work
 */
public interface OtherDocumentsService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'SUBMIT_OTHER_DOCUMENTS')")
    ServiceResult<Void> saveDocumentsSubmitDateTime(Long projectId, ZonedDateTime date);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileEntryResource> createCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileAndContents> getCollaborationAgreementFileContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    ServiceResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<Void> updateCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DELETE_OTHER_DOCUMENTS')")
    ServiceResult<Void> deleteCollaborationAgreementFile(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileEntryResource> createExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileAndContents> getExploitationPlanFileContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    ServiceResult<FileEntryResource> getExploitationPlanFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<Void> updateExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DELETE_OTHER_DOCUMENTS')")
    ServiceResult<Void> deleteExploitationPlanFile(Long projectId);

    //TODO IFS-471 - remove the boolean here and send enum throughout
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'ACCEPT_REJECT_OTHER_DOCUMENTS')")
    ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approval);
}