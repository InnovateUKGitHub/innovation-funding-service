package org.innovateuk.ifs.project.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;

import java.time.LocalDate;
import java.util.List;

/**
 * A service for dealing with Project Details Resources via the appropriate Rest services
 */
public interface ProjectDetailsService {

    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource address);

    ServiceResult<Void> setApplicationDetailsSubmitted(Long projectId);

    ServiceResult<Boolean> isSubmitAllowed(Long projectId);

    ServiceResult<Void> inviteFinanceContact (Long projectId, InviteProjectResource inviteProjectResource);

    ServiceResult<Void> inviteProjectManager (Long projectId, InviteProjectResource inviteProjectResource);

    ServiceResult<Void> saveProjectInvite(InviteProjectResource inviteProjectResource);

    ServiceResult<List<InviteProjectResource>> getInvitesByProject(Long projectId);
}
