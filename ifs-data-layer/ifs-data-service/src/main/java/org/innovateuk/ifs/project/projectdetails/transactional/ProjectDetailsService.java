package org.innovateuk.ifs.project.projectdetails.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;

/**
 * Transactional and secure service for Project Details processing work
 */
public interface ProjectDetailsService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<ProjectUserResource> getProjectManager(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    @SecuredBySpring(value = "UPDATE_PROJECT_DURATION", description = "Only project finance or IFS Admin can update the project duration")
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    ServiceResult<Void> updateProjectDuration(long projectId, long durationInMonths);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource addressResource);

    @PreAuthorize("hasPermission(#composite, 'UPDATE_FINANCE_CONTACT')")
    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    @PreAuthorize("hasPermission(#composite, 'UPDATE_PARTNER_PROJECT_LOCATION')")
    ServiceResult<Void> updatePartnerProjectLocation(ProjectOrganisationCompositeId composite, String postCode);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource);
}
