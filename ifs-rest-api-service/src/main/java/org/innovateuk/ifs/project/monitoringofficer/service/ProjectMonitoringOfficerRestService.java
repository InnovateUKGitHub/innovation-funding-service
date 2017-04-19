package org.innovateuk.ifs.project.monitoringofficer.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectMonitoringOfficerRestService {

    RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId);

    RestResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber);

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

    RestResult<Void> addPartner(Long projectId, Long userId, Long organisationId);

    RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getSignedGrantOfferLetterFileDetails(Long projectId);

    RestResult<FileEntryResource> addSignedGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<FileEntryResource> addGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> removeGrantOfferLetter(Long projectId);

    RestResult<Void> removeSignedGrantOfferLetter(Long projectId);

    RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId);

    RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId);

    RestResult<Void> submitGrantOfferLetter(Long projectId);

    RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    RestResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource);

    RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    RestResult<ProjectStatusResource> getProjectStatus(Long projectId);

    RestResult<ProjectUserResource> getProjectManager(Long projectId);

    RestResult<Void> sendGrantOfferLetter(Long projectId);

    RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);
}
