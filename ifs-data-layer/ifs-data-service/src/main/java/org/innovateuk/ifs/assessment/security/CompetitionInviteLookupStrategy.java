package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link AssessmentInvite}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionInviteLookupStrategy {
}
