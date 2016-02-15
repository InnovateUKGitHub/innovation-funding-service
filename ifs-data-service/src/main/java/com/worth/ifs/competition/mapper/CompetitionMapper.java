package com.worth.ifs.competition.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ApplicationMapper.class,
        QuestionMapper.class,
        SectionMapper.class
    }
)
public abstract class CompetitionMapper extends BaseMapper<Competition, CompetitionResource> {

    @Autowired
    public void setRepository(CrudRepository<Competition, Long> repository) {
        this.repository = repository;
    }

    public Long mapCompetitionToId(Competition object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Competition mapIdToCompetition(Long id) {
        return repository.findOne(id);
    }

}