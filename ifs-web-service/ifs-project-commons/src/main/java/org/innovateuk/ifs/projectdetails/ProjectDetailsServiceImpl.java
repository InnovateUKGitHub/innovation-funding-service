package org.innovateuk.ifs.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ProjectInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.projectdetails.service.ProjectDetailsRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * A service for dealing with Project Details Resources via the appropriate Rest services
 */
@Service
public class ProjectDetailsServiceImpl implements ProjectDetailsService {

    @Autowired
    private ProjectDetailsRestService projectDetailsRestService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Override
    public ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
        return projectDetailsRestService.updateProjectManager(projectId, projectManagerUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return projectDetailsRestService.updateFinanceContact(composite, financeContactUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updatePartnerProjectLocation(long projectId, long organisationId, String postcode) {
        return projectDetailsRestService.updatePartnerProjectLocation(projectId, organisationId, postcode).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return projectDetailsRestService.updateProjectStartDate(projectId, projectStartDate).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateProjectDuration(long projectId, long durationInMonths) {
        return projectDetailsRestService.updateProjectDuration(projectId, durationInMonths).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, AddressResource address) {
        return projectDetailsRestService.updateProjectAddress(leadOrganisationId, projectId, address).toServiceResult();
    }

    @Override
    public ServiceResult<Void> saveProjectInvite (ProjectInviteResource projectInviteResource) {
        return projectInviteRestService.saveProjectInvite(projectInviteResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> inviteFinanceContact(Long projectId, ProjectInviteResource projectInviteResource) {
        return projectDetailsRestService.inviteFinanceContact (projectId, projectInviteResource).toServiceResult();
    }

    @Override public ServiceResult<Void> inviteProjectManager(final Long projectId, final ProjectInviteResource projectInviteResource) {
        return projectDetailsRestService.inviteProjectManager (projectId, projectInviteResource).toServiceResult();
    }

    @Override
    public ServiceResult<List<ProjectInviteResource>>  getInvitesByProject (Long projectId) {
        return projectInviteRestService.getInvitesByProject (projectId).toServiceResult();
    }
}
