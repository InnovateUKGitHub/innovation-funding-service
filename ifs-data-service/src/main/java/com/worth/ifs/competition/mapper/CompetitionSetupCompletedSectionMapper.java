package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.CompetitionSetupCompletedSection;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            CompetitionMapper.class,
            CompetitionSetupSectionMapper.class
    }
)
public abstract class CompetitionSetupCompletedSectionMapper extends BaseMapper<CompetitionSetupCompletedSection, CompetitionSetupCompletedSectionResource, Long> {

    public Long mapCompetitionSetupCompletedSectionToId(CompetitionSetupCompletedSection object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}