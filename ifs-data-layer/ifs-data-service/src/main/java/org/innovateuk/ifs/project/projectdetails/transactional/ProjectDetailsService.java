package org.innovateuk.ifs.project.projectdetails.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Transactional and secure service for Project Details processing work
 */
public interface ProjectDetailsService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, AddressTypeEnum addressType, AddressResource addressResource);

    @PreAuthorize("hasPermission(#composite, 'UPDATE_FINANCE_CONTACT')")
    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> submitProjectDetails(Long projectId, ZonedDateTime date);

    @PreAuthorize("hasPermission(#projectId, 'UPDATE_FINANCE_CONTACT')")
    ServiceResult<Boolean> isSubmitAllowed(Long projectId);
}
