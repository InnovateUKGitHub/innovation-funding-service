package org.innovateuk.ifs.project;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
public interface ProjectService {

    List<ProjectUserResource> getProjectUsersForProject(Long projectId);

    List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId);

    ProjectResource getById(Long projectId);

    ProjectResource getByApplicationId(Long applicationId);

    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    ServiceResult<List<ProjectResource>> findByUser(Long userId);

    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource address);

    ServiceResult<Void> setApplicationDetailsSubmitted(Long projectId);

    ServiceResult<Boolean> isSubmitAllowed(Long projectId);

    OrganisationResource getLeadOrganisation(Long projectId);

    OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId);

    boolean isUserLeadPartner(Long projectId, Long userId);

    List<ProjectUserResource> getLeadPartners(Long projectId);

    List<ProjectUserResource> getPartners(Long projectId);

    Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId);

    Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId);

    Optional<ByteArrayResource> getAdditionalContractFile(Long projectId);

    Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId);

    Optional<ByteArrayResource> getGrantOfferFile(Long projectId);

    Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId);

    ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<Void> removeGrantOfferLetter(Long projectId);

    ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId);

    ServiceResult<Void> submitGrantOfferLetter(Long projectId);

    ProjectTeamStatusResource getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    ProjectStatusResource getProjectStatus(Long projectId);

    ServiceResult<Void> inviteFinanceContact (Long projectId, InviteProjectResource inviteProjectResource);

    ServiceResult<Void> inviteProjectManager (Long projectId, InviteProjectResource inviteProjectResource);

    List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId);

    ServiceResult<Void> saveProjectInvite(InviteProjectResource inviteProjectResource);

    ServiceResult<List<InviteProjectResource>> getInvitesByProject(Long projectId);

    ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    ServiceResult<Void> sendGrantOfferLetter(Long projectId);

    ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);

    Optional<ProjectUserResource> getProjectManager(Long projectId);

    Boolean isProjectManager(Long userId, Long projectId);
}
