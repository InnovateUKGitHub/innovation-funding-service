package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
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

    public MilestoneResource mapToResource(Milestone domain) {
        MilestoneResource resource = new MilestoneResource();
        resource.setId(domain.getId());
        resource.setDate(domain.getDate());
        resource.setType(domain.getType());
        if (domain.getCompetition() != null) {
            resource.setCompetitionId(domain.getCompetition().getId());
        }
        return resource;
    }

    public Milestone mapToDomain(MilestoneResource resource) {
        Milestone domain = new Milestone();
        domain.setId(resource.getId());
        domain.setType(resource.getType());
        domain.setDate(resource.getDate());
        Competition competition = new Competition();
        competition.setId(resource.getCompetitionId());
        domain.setCompetition(competition);
        return domain;
    }

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
