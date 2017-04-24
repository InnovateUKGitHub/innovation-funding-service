package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
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

    RestResult<Void> updateFinanceContact(ProjectOrganisationCompositeId compositeId, Long financeContactUserId);

    RestResult<List<ProjectUserResource>> getProjectUsersForProject(Long projectId);

    RestResult<ProjectResource> getByApplicationId(Long applicationId);

    RestResult<Void> setApplicationDetailsSubmitted(Long projectId);

    RestResult<Boolean> isSubmitAllowed(Long projectId);

    RestResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId);

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

    RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    RestResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource);

    RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    RestResult<ProjectStatusResource> getProjectStatus(Long projectId);

    RestResult<ProjectUserResource> getProjectManager(Long projectId);

}
