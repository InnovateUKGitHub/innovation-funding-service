package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        AssessmentPeriodMapper.class
    }
)
public abstract class MilestoneMapper extends BaseMapper<Milestone, MilestoneResource, Long> {

    @Mappings({
            @Mapping(source = "competition.id", target = "competitionId"),
            @Mapping(source = "assessmentPeriod.id", target = "assessmentPeriodId"),
    })
    @Override
    public abstract MilestoneResource mapToResource(Milestone domain);

    @Mappings({
            @Mapping(source = "competitionId", target = "competition"),
            @Mapping(source = "assessmentPeriodId", target = "assessmentPeriod"),
    })
    @Override
    public abstract Milestone mapToDomain(MilestoneResource resource);

    public abstract List<Milestone> mapToDomain(List<MilestoneResource> milestoneResources);

    public abstract List<MilestoneResource> mapToResource(List<Milestone> milestones);

    public Long mapMilestoneToId(Milestone object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
