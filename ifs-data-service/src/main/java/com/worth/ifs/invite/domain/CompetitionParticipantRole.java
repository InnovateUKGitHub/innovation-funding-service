package com.worth.ifs.invite.domain;

import com.worth.ifs.commons.util.enums.Identifiable;
import com.worth.ifs.competition.domain.Competition;

import java.util.Map;

import static com.worth.ifs.commons.util.enums.Identifiable.toIdMap;

/**
 * The role of a {@link CompetitionParticipant}.
 */
public enum CompetitionParticipantRole implements ParticipantRole<Competition>, Identifiable {
    ASSESSOR(1, "ASSESSOR");

    private final long id;
    private final String name;

    private static final Map<Long, CompetitionParticipantRole> idMap = toIdMap(values());

    CompetitionParticipantRole(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    public static CompetitionParticipantRole getById(long id) {
        return idMap.get(id);
    }
}
