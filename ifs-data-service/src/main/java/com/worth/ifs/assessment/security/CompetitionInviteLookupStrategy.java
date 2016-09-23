package com.worth.ifs.assessment.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.invite.domain.CompetitionInvite}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionInviteLookupStrategy {
}
