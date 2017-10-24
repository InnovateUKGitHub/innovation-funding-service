package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.competition.domain.Competition;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole<Competition> {
    INNOVATION_LEAD,
    ASSESSOR,
    PANEL_ASSESSOR
}
