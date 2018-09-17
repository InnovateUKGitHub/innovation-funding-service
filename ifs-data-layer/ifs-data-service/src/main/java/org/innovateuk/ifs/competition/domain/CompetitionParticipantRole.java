package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantRole;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole {
    INNOVATION_LEAD,
    STAKEHOLDER,
    ASSESSOR,
    PANEL_ASSESSOR,
    INTERVIEW_ASSESSOR
}