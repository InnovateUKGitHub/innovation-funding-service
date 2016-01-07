package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        QuestionMapper.class
    }
)
public abstract class SectionMapper {

    @Autowired
    private SectionRepository repository;

    public abstract SectionResource mapSectionToResource(Section object);

    public abstract Section resourceToSection(SectionResource resource);

    public Long mapSectionToId(Section object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Section mapIdToSection(Long id) {
        return repository.findOne(id);
    }

    public Long mapSectionResourceToId(SectionResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public SectionResource mapIdToSectionResource(Long id) {
        return mapSectionToResource(repository.findOne(id));
    }
}