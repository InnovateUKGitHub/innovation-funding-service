package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with Project Other Documents via the appropriate Rest services
 */
@Service
public class ProjectOtherDocumentsServiceImpl implements ProjectOtherDocumentsService {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Override
    public Optional<ByteArrayResource> getCollaborationAgreementFile(Long projectId) {
        return projectRestService.getCollaborationAgreementFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getCollaborationAgreementFileDetails(Long projectId) {
        return projectRestService.getCollaborationAgreementFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addCollaborationAgreementDocument(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeCollaborationAgreementDocument(Long projectId) {
        return projectRestService.removeCollaborationAgreementDocument(projectId).toServiceResult();
    }

    @Override
    public Optional<ByteArrayResource> getExploitationPlanFile(Long projectId) {
        return projectRestService.getExploitationPlanFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getExploitationPlanFileDetails(Long projectId) {
        return projectRestService.getExploitationPlanFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addExploitationPlanDocument(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeExploitationPlanDocument(Long projectId) {
        return projectRestService.removeExploitationPlanDocument(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved) {
        return projectRestService.acceptOrRejectOtherDocuments(projectId, approved).toServiceResult();
    }

    @Override
    public Boolean isOtherDocumentSubmitAllowed(Long projectId) {
        return projectRestService.isOtherDocumentsSubmitAllowed(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId) {
        return projectRestService.setPartnerDocumentsSubmitted(projectId).toServiceResult();
    }
}
