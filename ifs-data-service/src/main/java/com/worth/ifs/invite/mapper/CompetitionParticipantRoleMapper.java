package com.worth.ifs.invite.mapper;

import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link com.worth.ifs.invite.domain.CompetitionParticipantRole}.
 */
@Mapper(
        componentModel = "spring"
)
public interface CompetitionParticipantRoleMapper {

    public CompetitionParticipantRoleResource mapToResource(CompetitionParticipantRole domain);

    public  CompetitionParticipantRole mapToDomain(CompetitionParticipantRoleResource resource);
}
