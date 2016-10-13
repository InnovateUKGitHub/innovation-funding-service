package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;

@Service
public class ProjectRestServiceImpl extends BaseRestService implements ProjectRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<ProjectResource> getProjectById(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId, ProjectResource.class);
    }

    @Override
    public RestResult<SpendProfileResource> getSpendProfile(final Long projectId, final Long organisationId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/", SpendProfileResource.class);
    }

	@Override
	public RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
		return postWithRestResult(projectRestURL + "/" + projectId + "/project-manager/" + projectManagerUserId, Void.class);
	}
    @Override
    public RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/startdate?projectStartDate=" + projectStartDate, Void.class);
    }

    @Override
    public RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, OrganisationAddressType addressType, AddressResource address) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/address?addressType=" + addressType.name() + "&leadOrganisationId=" + leadOrganisationId, address, Void.class);
    }

    @Override
    public RestResult<List<ProjectResource>> findByUserId(long userId) {
        return getWithRestResult(projectRestURL + "/user/" + userId, projectResourceListType());
    }

	@Override
	public RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
		return postWithRestResult(projectRestURL + "/" + projectId + "/organisation/" + organisationId + "/finance-contact?financeContact=" + financeContactUserId, Void.class);
	}

    @Override
    public RestResult<List<ProjectUserResource>> getProjectUsersForProject(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/project-users", projectUserResourceList());
    }

    @Override
    public RestResult<ProjectResource> getByApplicationId(Long applicationId) {
        return getWithRestResult(projectRestURL + "/application/" + applicationId, ProjectResource.class);
    }

    @Override
    public RestResult<Void> setApplicationDetailsSubmitted(Long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/setApplicationDetailsSubmitted", Void.class);
    }

    @Override
    public RestResult<Boolean> isSubmitAllowed(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/isSubmitAllowed", Boolean.class);
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/getOrganisationByUser/" + userId, OrganisationResource.class);
    }

    @Override
    public RestResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        MonitoringOfficerResource monitoringOfficerData = new MonitoringOfficerResource(firstName, lastName, emailAddress, phoneNumber, projectId);
        return putWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerData, Void.class);
    }

    @Override
    public RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/monitoring-officer", MonitoringOfficerResource.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getCollaborationAgreementFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getCollaborationAgreementFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/collaboration-agreement?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeCollaborationAgreementDocument(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement");
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getExploitationPlanFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getExploitationPlanFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/exploitation-plan?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/ready", Boolean.class);
    }

    @Override
    public RestResult<Void> setPartnerDocumentsSubmitted(Long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/submit", Void.class);
    }

    @Override
    public RestResult<Void> removeExploitationPlanDocument(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan");
    }

    @Override
    public RestResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/approved/" + approved, Void.class);
    }

    @Override
    public RestResult<Void> addPartner(Long projectId, Long userId, Long organisationId) {
        return postWithRestResultAnonymous(projectRestURL + "/" + projectId + "/partners?userId=" + userId + "&organisationId=" + organisationId, Void.class);
    }

    @Override
    public RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId){
        return filterByUserId.
                map(userId -> getWithRestResult(projectRestURL + "/" + projectId + "/team-status?filterByUserId=" + userId, ProjectTeamStatusResource.class))
                .orElseGet(() -> getWithRestResult(projectRestURL + "/" + projectId + "/team-status", ProjectTeamStatusResource.class));
    }

    @Override
    public RestResult<ProjectStatusResource> getProjectStatus(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/status", ProjectStatusResource.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getSignedGrantOfferLetterFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addSignedGrantOfferLetterFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/signed-grant-offer?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<FileEntryResource> addGrantOfferLetterFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/grant-offer?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/additional-contract", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/additional-contract/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Void> submitGrantOfferLetter(Long projectId) {
        return  postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/submit", Void.class);
    }

    @Override
    public RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/additional-contract?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    public RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/invite-finance-contact", inviteResource, Void.class);
    }
}
