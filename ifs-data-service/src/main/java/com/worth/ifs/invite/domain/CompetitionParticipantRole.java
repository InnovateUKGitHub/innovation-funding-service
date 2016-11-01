package com.worth.ifs.invite.domain;

import com.worth.ifs.commons.util.enums.Identifiable;
import com.worth.ifs.competition.domain.Competition;

import java.util.Map;

import static com.worth.ifs.commons.util.enums.Identifiable.toIdMap;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole<Competition> {
    ASSESSOR;
}
