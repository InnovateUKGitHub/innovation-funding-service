package com.worth.ifs.invite.mapper;


import com.worth.ifs.assessment.mapper.CompetitionInviteMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        UserMapper.class,
        CompetitionInviteMapper.class,
        RejectionReasonMapper.class,
        CompetitionParticipantRoleMapper.class,
        ParticipantStatusMapper.class,
        CompetitionInviteMapper.class
    }
)
public abstract class CompetitionParticipantMapper extends BaseMapper<CompetitionParticipant, CompetitionParticipantResource, Long> {

    @Mappings({
            @Mapping(source = "process.id", target = "competitionId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "process.name", target = "competitionName"),
            @Mapping(source = "process.assessorAcceptsDate", target = "assessorAcceptsDate"),
            @Mapping(source = "process.assessorDeadlineDate", target = "assessorDeadlineDate"),
    })
    @Override
    public abstract CompetitionParticipantResource mapToResource(CompetitionParticipant domain);

    public Long mapCompetitionParticipantToId(CompetitionParticipant object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
