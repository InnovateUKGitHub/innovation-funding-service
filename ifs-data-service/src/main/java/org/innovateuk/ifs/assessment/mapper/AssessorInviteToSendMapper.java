package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@Link CompetitionInvite} and {@Link AssessorInviteToSendMapper}.
 */

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AssessorInviteToSendMapper extends BaseMapper<CompetitionInvite, AssessorInviteToSendResource, Long> {

    @Mappings({
            @Mapping(source = "name",target = "recipient"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(target = "content", ignore = true)

    })
    @Override
    public abstract AssessorInviteToSendResource mapToResource(CompetitionInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
            @Mapping(target="email", ignore=true),
    })
    @Override
    public abstract CompetitionInvite mapToDomain(AssessorInviteToSendResource resource);

}
