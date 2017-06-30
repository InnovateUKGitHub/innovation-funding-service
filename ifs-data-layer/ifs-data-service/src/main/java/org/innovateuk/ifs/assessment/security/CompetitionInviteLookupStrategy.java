package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link org.innovateuk.ifs.invite.domain.CompetitionInvite}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionInviteLookupStrategy {
}
