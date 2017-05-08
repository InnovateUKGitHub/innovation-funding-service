package org.innovateuk.ifs.project.otherdocuments.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface ProjectOtherDocumentsRestService {

    RestResult<Optional<ByteArrayResource>> getCollaborationAgreementFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getCollaborationAgreementFileDetails(Long projectId);

    RestResult<Optional<ByteArrayResource>> getExploitationPlanFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getExploitationPlanFileDetails(Long projectId);

    RestResult<Void> removeCollaborationAgreementDocument(Long projectId);

    RestResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> removeExploitationPlanDocument(Long projectId);

    RestResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved);

    RestResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId);

    RestResult<Void> setPartnerDocumentsSubmitted(Long projectId);
}
