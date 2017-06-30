package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link CompetitionInvite} and {@link CompetitionInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class CompetitionInviteMapper extends BaseMapper<CompetitionInvite, CompetitionInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "target.assessorAcceptsDate", target = "acceptsDate"),
            @Mapping(source = "target.assessorDeadlineDate", target = "deadlineDate"),
            @Mapping(source = "target.assessorPay", target = "assessorPay"),
            @Mapping(source = "target.assessorBriefingDate", target = "briefingDate"),
            @Mapping(source = "innovationAreaOrNull", target = "innovationArea"),
    })
    @Override
    public abstract CompetitionInviteResource mapToResource(CompetitionInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract CompetitionInvite mapToDomain(CompetitionInviteResource resource);

    public Long mapCompetiionInviteToId(CompetitionInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
