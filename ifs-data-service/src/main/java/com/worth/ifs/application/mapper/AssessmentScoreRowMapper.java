package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.FormInputGuidanceRow;
import com.worth.ifs.application.resource.AssessmentScoreRowResource;
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
public abstract class AssessmentScoreRowMapper extends BaseMapper<FormInputGuidanceRow, AssessmentScoreRowResource, Long> {

    public Long questionToId(FormInputGuidanceRow formInputGuidanceRow){
        return formInputGuidanceRow.getId();
    }

    @Override
    public abstract AssessmentScoreRowResource mapToResource(FormInputGuidanceRow domain);

    @Override
    @Mappings({
        @Mapping(target = "questionAssessment", ignore = true)
    })
    public abstract FormInputGuidanceRow mapToDomain(AssessmentScoreRowResource resource);
}
