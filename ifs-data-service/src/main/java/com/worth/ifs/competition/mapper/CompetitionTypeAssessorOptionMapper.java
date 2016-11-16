package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            CompetitionTypeMapper.class,
    }
)
public abstract class CompetitionTypeAssessorOptionMapper extends
        BaseMapper<CompetitionTypeAssessorOption, CompetitionTypeAssessorOptionResource, Long> {

    public abstract CompetitionTypeAssessorOptionResource mapToResource(CompetitionTypeAssessorOption domain);
}