package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.AssessmentScoreRow;
import com.worth.ifs.application.domain.QuestionAssessment;
import com.worth.ifs.application.resource.AssessmentScoreRowResource;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class
//    uses = {
//    }
)
public abstract class AssessmentScoreRowMapper extends BaseMapper<AssessmentScoreRow, AssessmentScoreRowResource, Long> {

    public Long questionToId(AssessmentScoreRow assessmentScoreRow){
        return assessmentScoreRow.getId();
    }

    @Override
    public abstract AssessmentScoreRowResource mapToResource(AssessmentScoreRow domain);

    @Override
    @Mappings({
        @Mapping(target = "questionAssessment", ignore = true)
    })
    public abstract AssessmentScoreRow mapToDomain(AssessmentScoreRowResource resource);
}
