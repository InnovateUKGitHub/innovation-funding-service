package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.invite.domain.competition.CompetitionParticipantRole;
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
