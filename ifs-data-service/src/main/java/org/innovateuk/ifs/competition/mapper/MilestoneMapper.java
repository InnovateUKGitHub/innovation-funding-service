package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class
    }
)
public abstract class MilestoneMapper extends BaseMapper<Milestone, MilestoneResource, Long> {

    public abstract MilestoneResource mapToResource(Milestone domain);

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
