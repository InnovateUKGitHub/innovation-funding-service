package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class
    }
)
public abstract class MilestoneMapper extends BaseMapper<Milestone, MilestoneResource, Long> {

    @Mappings(
            @Mapping(source = "competition.id", target = "competitionId")
    )
    public abstract MilestoneResource mapToResource(Milestone domain);

    @Mappings(
            @Mapping(source = "competitionId", target = "competition")
    )
    public abstract Milestone mapToDomain(MilestoneResource resource);

    public Long mapMilestoneToId(Milestone object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Milestone build() {
        return createDefault(Milestone.class);
    }

}
