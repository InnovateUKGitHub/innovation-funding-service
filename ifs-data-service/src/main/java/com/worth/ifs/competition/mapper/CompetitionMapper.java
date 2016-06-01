package com.worth.ifs.competition.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ApplicationMapper.class,
        QuestionMapper.class,
        SectionMapper.class
    }
)
public abstract class CompetitionMapper extends BaseMapper<Competition, CompetitionResource, Long> {

    @Mappings({
            @Mapping(source = "competitionStatus", target = "competitionStatus"),
    })
    @Override
    public abstract CompetitionResource mapToResource(Competition domain);

    @Mappings({
            @Mapping(target = "status", ignore=true)
    })
    public abstract Competition mapToDomain(CompetitionResource domain);

    public Long mapCompetitionToId(Competition object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}