package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.User} and {@link org.innovateuk.ifs.invite.domain.CompetitionInvite} data related to assesors.
 */
public interface AssessorService {
    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource);
}
