package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.User} and {@link AssessmentInvite} data related to assesors.
 */
public interface AssessorService {
    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource);

    @PreAuthorize("hasPermission(#assessorId, 'org.innovateuk.ifs.assessment.resource.AssessorProfileResource', 'READ_PROFILE')")
    ServiceResult<AssessorProfileResource> getAssessorProfile(long assessorId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "NOTIFY_ASSESSORS",
            description = "Comp admins and execs can notify all assessors of their assignments for a competition")
    ServiceResult<Void> notifyAssessorsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "HAS_ASSESSMENTS",
            description = "Comp admins and execs can see if an assessor has any assessments assigned to them")
    ServiceResult<Boolean> hasApplicationsAssigned(long assessorId);
}
