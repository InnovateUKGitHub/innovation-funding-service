package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.finance.mapper.FinanceRowMapper;
import com.worth.ifs.form.mapper.FormInputMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        SectionMapper.class,
        CompetitionMapper.class,
        QuestionStatusMapper.class,
        FormInputMapper.class,
        FinanceRowMapper.class
    }
)
public abstract class QuestionMapper extends BaseMapper<Question, QuestionResource, Long> {

    public Long questionToId(Question question){
        return question.getId();
    }

    public abstract QuestionResource mapToResource(Question domain);

    @Override
    @Mappings({
            @Mapping(target = "costs", ignore = true),
            @Mapping(target = "questionStatuses", ignore = true)
    })
    public abstract Question mapToDomain(QuestionResource resource);
}
