package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
    }
)
public abstract class CompetitionTypeMapper extends BaseMapper<CompetitionType, CompetitionTypeResource, Long> {

    public Long mapCompetitionTypeToId(CompetitionType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}