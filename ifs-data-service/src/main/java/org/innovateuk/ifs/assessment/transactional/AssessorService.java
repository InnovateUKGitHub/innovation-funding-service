package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.User} and {@link org.innovateuk.ifs.invite.domain.CompetitionInvite} data related to assesors.
 */
public interface AssessorService {
    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "READ",
            securedType = ProfileResource.class,
            description = "Comp Admins can read any Assessor profile")
    ServiceResult<AssessorProfileResource> getAssessorProfile(Long assessorId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "NOTIFY_ASSESSORS",
            description = "Comp admins and execs can notify all assessors of their assignments for a competition")
    ServiceResult<Void> notifyAssessorsByCompetition(long competitionId);
}
