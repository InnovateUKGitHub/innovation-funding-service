package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
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
