package com.worth.ifs.assessment.security;

import com.worth.ifs.security.PermissionEntityLookupStrategies;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.invite.domain.CompetitionParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionParticipantLookupStrategy {
}
