package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentInvite;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link CompetitionAssessmentInvite}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionInviteLookupStrategy {
}
