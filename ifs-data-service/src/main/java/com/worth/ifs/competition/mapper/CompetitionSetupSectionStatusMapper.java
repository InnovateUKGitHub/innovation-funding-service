package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.CompetitionSetupSectionStatus;
import com.worth.ifs.competition.resource.CompetitionSetupSectionStatusResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            CompetitionMapper.class,
            CompetitionSetupSectionMapper.class
    }
)
public abstract class CompetitionSetupSectionStatusMapper extends BaseMapper<CompetitionSetupSectionStatus, CompetitionSetupSectionStatusResource, Long> {

    public Long mapCompetitionSetupSectionStatusToId(CompetitionSetupSectionStatus object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}