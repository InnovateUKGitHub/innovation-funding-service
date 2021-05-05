package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link CompetitionParticipantRole}.
 */
@Mapper(
        componentModel = "spring"
)
public interface CompetitionParticipantRoleMapper {

    public CompetitionParticipantRoleResource mapToResource(CompetitionParticipantRole domain);

    public CompetitionParticipantRole mapToDomain(CompetitionParticipantRoleResource resource);
}
