package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.AssessorCountOption;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            CompetitionTypeMapper.class,
    }
)
public abstract class AssessorCountOptionMapper extends
        BaseMapper<AssessorCountOption, AssessorCountOptionResource, Long> {

    public abstract AssessorCountOptionResource mapToResource(AssessorCountOption domain);
}