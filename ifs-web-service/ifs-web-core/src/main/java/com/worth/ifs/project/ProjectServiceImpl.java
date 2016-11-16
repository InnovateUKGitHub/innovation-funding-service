package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.service.ProjectInviteRestService;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.rest.RestResult.aggregate;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.util.CollectionFunctions.*;

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
    public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
        return projectRestService.updateFinanceContact(projectId, organisationId, financeContactUserId).toServiceResult();
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
    public ServiceResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        return projectRestService.updateMonitoringOfficer(projectId, firstName, lastName, emailAddress, phoneNumber).toServiceResult();
    }

    @Override
    public Optional<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId) {
        return projectRestService.getMonitoringOfficerForProject(projectId).toOptionalIfNotFound().
                getSuccessObjectOrThrowException();
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
    public Optional<ByteArrayResource> getGeneratedGrantOfferFile(Long projectId) {
        return projectRestService.getGrantOfferFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getGeneratedGrantOfferFileDetails(Long projectId) {
        return projectRestService.getGrantOfferFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addSignedGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> addGeneratedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return projectRestService.addGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
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
}
