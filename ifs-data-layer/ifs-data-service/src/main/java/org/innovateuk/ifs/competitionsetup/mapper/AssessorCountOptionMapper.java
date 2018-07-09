package org.innovateuk.ifs.competitionsetup.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
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
