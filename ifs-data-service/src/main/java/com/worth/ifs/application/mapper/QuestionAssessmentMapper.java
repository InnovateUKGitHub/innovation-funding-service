package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionAssessment;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
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
        AssessmentScoreRowMapper.class,
        QuestionMapper.class
    }
)
public abstract class QuestionAssessmentMapper extends BaseMapper<QuestionAssessment, QuestionAssessmentResource, Long> {

    public Long questionToId(QuestionAssessment questionAssessment){
        if (questionAssessment != null) {
            return questionAssessment.getId();
        } else {
            return null;
        }
    }

    @Override
    public abstract QuestionAssessmentResource mapToResource(QuestionAssessment domain);

    @Override
    @Mappings({
            @Mapping(target = "question", ignore = true)
    })
    public abstract QuestionAssessment mapToDomain(QuestionAssessmentResource resource);
}
