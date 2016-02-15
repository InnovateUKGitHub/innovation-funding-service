package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        QuestionMapper.class
    }
)
public abstract class SectionMapper extends BaseMapper<Section, SectionResource> {

    @Autowired
    public void setRepository(CrudRepository<Section, Long> repository) {
        this.repository = repository;
    }

    public Long mapSectionToId(Section object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Section mapIdToSection(Long id) {
        return repository.findOne(id);
    }

    public SectionResource mapIdToSectionResource(Long id) {
        return mapToResource(repository.findOne(id));
    }
}