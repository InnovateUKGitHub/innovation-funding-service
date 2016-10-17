package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.User} and {@link com.worth.ifs.invite.domain.CompetitionInvite} data related to assesors.
 */
public interface AssessorService {
    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource);
}
