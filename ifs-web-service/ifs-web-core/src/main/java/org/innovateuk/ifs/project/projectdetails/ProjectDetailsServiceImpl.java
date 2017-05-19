package org.innovateuk.ifs.project.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.service.ProjectRestService;
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
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Override
    public ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
        return projectRestService.updateProjectManager(projectId, projectManagerUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return projectRestService.updateFinanceContact(composite, financeContactUserId).toServiceResult();
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
