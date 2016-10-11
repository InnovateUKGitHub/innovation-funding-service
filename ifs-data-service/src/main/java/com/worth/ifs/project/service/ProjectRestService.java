package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);

    RestResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, OrganisationAddressType addressType, AddressResource address);

    RestResult<List<ProjectResource>> findByUserId(long userId);

    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);

    RestResult<List<ProjectUserResource>> getProjectUsersForProject(Long projectId);

    RestResult<ProjectResource> getByApplicationId(Long applicationId);

    RestResult<Void> setApplicationDetailsSubmitted(Long projectId);

    RestResult<Boolean> isSubmitAllowed(Long projectId);

    RestResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId);

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

    RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId);

    RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId);

    RestResult<Void> submitGrantOfferLetter(Long projectId);

    RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);
}