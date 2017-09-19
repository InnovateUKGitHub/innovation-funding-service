package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;

import java.time.LocalDate;

/**
 * REST service for Project Details related operations
 */
public interface ProjectDetailsRestService {

    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, OrganisationAddressType addressType, AddressResource address);

    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    RestResult<Void> updateFinanceContact(ProjectOrganisationCompositeId compositeId, Long financeContactUserId);

    RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    RestResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource);
}
