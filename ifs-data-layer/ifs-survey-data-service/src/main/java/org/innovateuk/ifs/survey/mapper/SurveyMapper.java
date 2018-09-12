package org.innovateuk.ifs.survey.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.domain.Survey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class SurveyMapper extends BaseMapper<Survey, SurveyResource, Long> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    @Override
    public abstract Survey mapToDomain(SurveyResource resource);


}