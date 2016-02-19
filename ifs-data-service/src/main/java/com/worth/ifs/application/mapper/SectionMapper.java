package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        QuestionMapper.class
    }
)
public abstract class SectionMapper extends BaseMapper<Section, SectionResource, Long> {

    public Long mapSectionToId(Section object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public SectionResource mapIdToSectionResource(Long id) {
        return mapToResource(repository.findOne(id));
    }
}