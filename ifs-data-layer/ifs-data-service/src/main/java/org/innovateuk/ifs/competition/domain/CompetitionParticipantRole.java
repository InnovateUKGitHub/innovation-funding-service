package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantRole;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole<Competition> {
    INNOVATION_LEAD,
    ASSESSOR,
    PANEL_ASSESSOR,
    INTERVIEW_ASSESSOR
}