package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.mapstruct.*;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        SectionMapper.class,
        CompetitionMapper.class,
        FormInputMapper.class
    }
)
public abstract class QuestionMapper extends BaseMapper<Question, QuestionResource, Long> {

    public Long questionToId(Question question){
        return question.getId();
    }

    public abstract QuestionResource mapToResource(Question domain);

    @Mappings({
            @Mapping(target = "formInputs", ignore = true)
    })
    public abstract Question mapToDomain(QuestionResource resource);
}
