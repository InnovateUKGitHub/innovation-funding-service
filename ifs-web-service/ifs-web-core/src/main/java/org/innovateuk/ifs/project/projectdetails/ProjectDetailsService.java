package org.innovateuk.ifs.project.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;

import java.time.LocalDate;
import java.util.List;

/**
 * A service for dealing with Project Details Resources via the appropriate Rest services
 */
public interface ProjectDetailsService {

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource address);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> inviteFinanceContact (Long projectId, InviteProjectResource inviteProjectResource);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> inviteProjectManager (Long projectId, InviteProjectResource inviteProjectResource);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveProjectInvite(InviteProjectResource inviteProjectResource);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<List<InviteProjectResource>> getInvitesByProject(Long projectId);
}
