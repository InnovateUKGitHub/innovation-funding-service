package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class CompetitionSetupSectionMapper extends BaseMapper<CompetitionSetupSection, CompetitionSetupSectionResource, Long> {

    public Long mapCompetitionSetupSectionToId(CompetitionSetupSection object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}