package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.finance.mapper.CostMapper;
import com.worth.ifs.form.mapper.FormInputMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        SectionMapper.class,
        CompetitionMapper.class,
        QuestionStatusMapper.class,
        ResponseMapper.class,
        FormInputMapper.class,
        CostMapper.class
    }
)
public abstract class QuestionMapper {

    @Autowired
    private QuestionRepository questionRepository;
    
    public abstract QuestionResource questionToResource(Question question);
    public abstract Question resourceToQuestion(QuestionResource resource);

    public Long questionToId(Question question){
        return question.getId();
    }

    public Question idToQuestion(Long id){
        if (id == null) {
            return null;
        }
        return questionRepository.findOne(id);
    }
}
