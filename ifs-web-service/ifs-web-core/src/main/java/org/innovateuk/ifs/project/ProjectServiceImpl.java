package org.innovateuk.ifs.project;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Override
    public List<ProjectUserResource> getProjectUsersForProject(Long projectId) {
        return projectRestService.getProjectUsersForProject(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getByApplicationId(Long applicationId) {
        if(applicationId == null) {
            return null;
        }
        RestResult<ProjectResource> restResult = projectRestService.getByApplicationId(applicationId);
        if(restResult.isSuccess()){
            return restResult.getSuccessObject();
        } else {
            return null;
        }
    }

    @Override
    public ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
        return projectRestService.updateProjectManager(projectId, projectManagerUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return projectRestService.updateFinanceContact(composite, financeContactUserId).toServiceResult();
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUser(Long userId) {
        return projectRestService.findByUserId(userId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return projectRestService.updateProjectStartDate(projectId, projectStartDate).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource address) {
        return projectRestService.updateProjectAddress(leadOrganisationId, projectId, addressType, address).toServiceResult();
    }

    @Override
    public ServiceResult<Void> setApplicationDetailsSubmitted(Long projectId) {
        return projectRestService.setApplicationDetailsSubmitted(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
        return projectRestService.isSubmitAllowed(projectId).toServiceResult();
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long projectId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
        return applicationService.getLeadOrganisation(project.getApplication());
    }

    @Override
    public OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId) {
        return projectRestService.getOrganisationByProjectAndUser(projectId, userId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId) {

        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);

        List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
        List<RestResult<OrganisationResource>> organisationResults = simpleMap(organisationIds, organisationRestService::getOrganisationById);
        RestResult<List<OrganisationResource>> organisationResultsCombined = aggregate(organisationResults);

        return organisationResultsCombined.getSuccessObjectOrThrowException();
    }

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
    public List<ProjectUserResource> getLeadPartners(Long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> projectUser.getOrganisation().equals(leadOrganisation.getId()));
    }

    @Override
    public List<ProjectUserResource> getPartners(Long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> !(projectUser.getOrganisation().equals(leadOrganisation.getId())));
    }

    @Override
    public Boolean isOtherDocumentSubmitAllowed(Long projectId) {
        return projectRestService.isOtherDocumentsSubmitAllowed(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> setPartnerDocumentsSubmitted(Long projectId) {
        return projectRestService.setPartnerDocumentsSubmitted(projectId).toServiceResult();
    }

    @Override
    public boolean isUserLeadPartner(Long projectId, Long userId) {
        return !simpleFilter(getLeadPartners(projectId), projectUser -> projectUser.getUser().equals(userId)).isEmpty();
    }

    @Override
    public ProjectTeamStatusResource getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId){
        return projectRestService.getProjectTeamStatus(projectId, filterByUserId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectStatusResource getProjectStatus(Long projectId) {
        return projectRestService.getProjectStatus(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId) {
        return projectRestService.getSignedGrantOfferLetterFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId) {
        return projectRestService.getSignedGrantOfferLetterFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<ByteArrayResource> getAdditionalContractFile(Long projectId) {
        return projectRestService.getAdditionalContractFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId) {
        return projectRestService.getAdditionalContractFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<ByteArrayResource> getGrantOfferFile(Long projectId) {
        return projectRestService.getGrantOfferFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId) {
        return projectRestService.getGrantOfferFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addSignedGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeGrantOfferLetter(Long projectId) {
        return projectRestService.removeGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId) {
        return projectRestService.removeSignedGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return projectRestService.submitGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId) {
        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);
        return simpleFilter(projectUsers, pu -> PARTNER.getName().equals(pu.getRoleName()));
    }

    @Override
    public ServiceResult<Void> saveProjectInvite (InviteProjectResource inviteProjectResource) {
        return projectInviteRestService.saveProjectInvite(inviteProjectResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> inviteFinanceContact (Long projectId, InviteProjectResource inviteProjectResource) {
        return projectRestService.inviteFinanceContact (projectId, inviteProjectResource).toServiceResult();
    }

    @Override public ServiceResult<Void> inviteProjectManager(final Long projectId, final InviteProjectResource inviteProjectResource) {
        return projectRestService.inviteProjectManager (projectId, inviteProjectResource).toServiceResult();
    }

    @Override
    public ServiceResult<List<InviteProjectResource>>  getInvitesByProject (Long projectId) {
        return projectInviteRestService.getInvitesByProject (projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {
        return projectRestService.sendGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
        return projectRestService.isSendGrantOfferLetterAllowed(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addAdditionalContractFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return projectRestService.isGrantOfferLetterAlreadySent(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {
        return projectRestService.approveOrRejectSignedGrantOfferLetter(projectId, approvalType).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return projectRestService.isSignedGrantOfferLetterApproved(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return projectRestService.getGrantOfferLetterWorkflowState(projectId).toServiceResult();
    }

    @Override
    public Optional<ProjectUserResource> getProjectManager(Long projectId) {
        return projectRestService.getProjectManager(projectId).toServiceResult().getOptionalSuccessObject();
    }

    @Override
    public final Boolean isProjectManager(Long userId, Long projectId) {
        return getProjectManager(projectId).map(maybePM -> maybePM.isUser(userId)).orElse(false);
    }

}
