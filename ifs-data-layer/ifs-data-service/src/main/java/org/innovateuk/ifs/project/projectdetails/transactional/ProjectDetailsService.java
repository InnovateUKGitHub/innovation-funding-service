package org.innovateuk.ifs.project.projectdetails.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
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
    @Activity(projectId = "projectId", type = ActivityType.PROJECT_MANAGER_NOMINATED)
    ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    @SecuredBySpring(value = "UPDATE_PROJECT_DURATION", description = "Only project finance or IFS Admin can update the project duration")
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    ServiceResult<Void> updateProjectDuration(long projectId, long durationInMonths);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPDATE_BASIC_PROJECT_SETUP_DETAILS')")
    ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, AddressResource addressResource);

    @PreAuthorize("hasPermission(#composite, 'UPDATE_FINANCE_CONTACT')")
    @Activity(projectOrganisationCompositeId = "composite", type = ActivityType.FINANCE_CONTACT_NOMINATED)
    ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId);

    @PreAuthorize("hasPermission(#composite, 'UPDATE_PARTNER_PROJECT_LOCATION')")
    ServiceResult<Void> updatePartnerProjectLocation(ProjectOrganisationCompositeId composite, String postcode);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteFinanceContact(Long projectId, ProjectUserInviteResource inviteResource);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteProjectManager(Long projectId, ProjectUserInviteResource inviteResource);
}
