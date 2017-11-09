package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * A service for dealing with Project Other Documents via the appropriate Rest services
 */
public interface OtherDocumentsService {


    @NotSecured("Not currently secured")
    Optional<ByteArrayResource> getCollaborationAgreementFile(Long projectId);

    @NotSecured("Not currently secured")
    Optional<FileEntryResource> getCollaborationAgreementFileDetails(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured("Not currently secured")
    ServiceResult<Void> removeCollaborationAgreementDocument(Long projectId);

    @NotSecured("Not currently secured")
    Optional<ByteArrayResource> getExploitationPlanFile(Long projectId);

    @NotSecured("Not currently secured")
    Optional<FileEntryResource> getExploitationPlanFileDetails(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured("Not currently secured")
    ServiceResult<Void> removeExploitationPlanDocument(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved);

    @NotSecured("Not currently secured")
    Boolean isOtherDocumentSubmitAllowed(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId);

}
