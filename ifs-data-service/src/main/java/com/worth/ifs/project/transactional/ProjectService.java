package com.worth.ifs.project.transactional;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.security.SecuredBySpring;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId);

    @NotSecured(value="", mustBeSecuredByOtherServices = false) // TODO - This needs to be changed once Security is added
    ServiceResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProjectResource> getByApplicationId(@P("applicationId") Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectResource>> findAll();

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin is able to create a project (by making decision)" )
    ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin is able to create a projects (by making decisions)" )
    ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
	ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource addressResource);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectResource>> findByUserId(Long userId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_FINANCE_CONTACT')")
    ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);

    @PreAuthorize("hasPermission(#inviteResource, 'INVITE_FINANCE_CONTACT')")
    ServiceResult<Void> inviteFinanceContact(Long projectId, InviteResource inviteResource);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> saveProjectSubmitDateTime(Long projectId, LocalDateTime date);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPDATE_FINANCE_CONTACT')")
    ServiceResult<Boolean> isSubmitAllowed(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'VIEW_MONITORING_OFFICER')")
    ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'ASSIGN_MONITORING_OFFICER')")
    ServiceResult<Void> saveMonitoringOfficer(Long projectId, MonitoringOfficerResource monitoringOfficerResource);

 	@PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId);

    @PreAuthorize("hasPermission(#monitoringOfficer.project, 'com.worth.ifs.project.resource.ProjectResource', 'ASSIGN_MONITORING_OFFICER')")
    ServiceResult<Void> notifyStakeholdersOfMonitoringOfficerChange(MonitoringOfficerResource monitoringOfficer);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileEntryResource> createCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileAndContents> getCollaborationAgreementFileContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    ServiceResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<Void> updateCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'DELETE_OTHER_DOCUMENTS')")
    ServiceResult<Void> deleteCollaborationAgreementFile(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileEntryResource> createExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileAndContents> getExploitationPlanFileContents(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    ServiceResult<FileEntryResource> getExploitationPlanFileEntryDetails(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<Void> updateExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'DELETE_OTHER_DOCUMENTS')")
    ServiceResult<Void> deleteExploitationPlanFile(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    List<ServiceResult<FileAndContents>>  retrieveUploadedDocuments(Long projectId);
}
