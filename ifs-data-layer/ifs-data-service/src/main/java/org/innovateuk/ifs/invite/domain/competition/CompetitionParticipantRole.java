package org.innovateuk.ifs.invite.domain.competition;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ParticipantRole;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole<Competition> {
    INNOVATION_LEAD,
    ASSESSOR,
    PANEL_ASSESSOR
}