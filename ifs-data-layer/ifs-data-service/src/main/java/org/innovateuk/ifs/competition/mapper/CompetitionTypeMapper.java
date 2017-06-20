package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
    }
)
public abstract class CompetitionTypeMapper extends BaseMapper<CompetitionType, CompetitionTypeResource, Long> {

    @Mappings({
            @Mapping(target = "template", ignore = true)
    })
    public abstract CompetitionType mapToDomain(CompetitionTypeResource domain);

    public Long mapCompetitionTypeToId(CompetitionType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
