package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * A service for dealing with Project Other Documents via the appropriate Rest services
 */
public interface ProjectOtherDocumentsService {


    Optional<ByteArrayResource> getCollaborationAgreementFile(Long projectId);

    Optional<FileEntryResource> getCollaborationAgreementFileDetails(Long projectId);

    ServiceResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<Void> removeCollaborationAgreementDocument(Long projectId);

    Optional<ByteArrayResource> getExploitationPlanFile(Long projectId);

    Optional<FileEntryResource> getExploitationPlanFileDetails(Long projectId);

    ServiceResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<Void> removeExploitationPlanDocument(Long projectId);

    ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved);

    Boolean isOtherDocumentSubmitAllowed(Long projectId);

    ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId);

}
