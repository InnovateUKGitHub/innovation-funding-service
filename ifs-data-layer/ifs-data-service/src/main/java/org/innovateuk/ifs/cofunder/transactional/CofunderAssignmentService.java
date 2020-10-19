package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CofunderAssignmentService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can read assignments")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can read assignments")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<CofunderAssignmentResource>> getAssignmentsByApplicationId(long applicationId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can assign")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<CofunderAssignmentResource> assign(long userId, long applicationId);

    @SecuredBySpring(value = "UUPDATE", description = "Only those with either comp admin, project finance or ifs_administrator roles can assign")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> assign(List<Long> userId, long applicationId);

    @SecuredBySpring(value = "DELETE", description = "Only those with either comp admin, project finance or ifs_administrator roles can unassign")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> removeAssignment(long userId, long applicationId);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin, project finance or ifs_administrator roles can read assignments")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> decision(long assignmentId, CofunderDecisionResource decision);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin, project finance or ifs_administrator roles can edit assignments")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> edit(long assignmentId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can find applications needing cofunders")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, String filter, Pageable pageable);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can find available cofunders")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<CofundersAvailableForApplicationPageResource> findAvailableCofundersForApplication(long applicationId, String filter, Pageable pageable);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance or ifs_administrator roles can find available cofunders")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<List<Long>> findAvailableCofundersUserIdsForApplication(long applicationId, String filter);

}
