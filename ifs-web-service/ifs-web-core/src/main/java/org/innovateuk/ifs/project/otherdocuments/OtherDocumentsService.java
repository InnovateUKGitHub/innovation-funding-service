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


    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ByteArrayResource> getCollaborationAgreementFile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<FileEntryResource> getCollaborationAgreementFileDetails(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeCollaborationAgreementDocument(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ByteArrayResource> getExploitationPlanFile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<FileEntryResource> getExploitationPlanFileDetails(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeExploitationPlanDocument(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean isOtherDocumentSubmitAllowed(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId);

}
