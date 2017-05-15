package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.otherdocuments.service.OtherDocumentsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with Project Other Documents via the appropriate Rest services
 */
@Service
public class OtherDocumentsServiceImpl implements OtherDocumentsService {

    @Autowired
    private OtherDocumentsRestService otherDocumentsRestService;

    @Override
    public Optional<ByteArrayResource> getCollaborationAgreementFile(Long projectId) {
        return otherDocumentsRestService.getCollaborationAgreementFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getCollaborationAgreementFileDetails(Long projectId) {
        return otherDocumentsRestService.getCollaborationAgreementFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return otherDocumentsRestService.addCollaborationAgreementDocument(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeCollaborationAgreementDocument(Long projectId) {
        return otherDocumentsRestService.removeCollaborationAgreementDocument(projectId).toServiceResult();
    }

    @Override
    public Optional<ByteArrayResource> getExploitationPlanFile(Long projectId) {
        return otherDocumentsRestService.getExploitationPlanFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getExploitationPlanFileDetails(Long projectId) {
        return otherDocumentsRestService.getExploitationPlanFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return otherDocumentsRestService.addExploitationPlanDocument(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeExploitationPlanDocument(Long projectId) {
        return otherDocumentsRestService.removeExploitationPlanDocument(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved) {
        return otherDocumentsRestService.acceptOrRejectOtherDocuments(projectId, approved).toServiceResult();
    }

    @Override
    public Boolean isOtherDocumentSubmitAllowed(Long projectId) {
        return otherDocumentsRestService.isOtherDocumentsSubmitAllowed(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId) {
        return otherDocumentsRestService.setPartnerDocumentsSubmitted(projectId).toServiceResult();
    }
}
