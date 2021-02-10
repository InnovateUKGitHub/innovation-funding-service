package org.innovateuk.ifs.questionnaire.response.mapper;


import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.mapper.QuestionnaireMapper;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                QuestionnaireMapper.class,
                QuestionnaireQuestionResponseMapper.class
        }
)
public abstract class QuestionnaireResponseMapper extends BaseResourceMapper<QuestionnaireResponse, QuestionnaireResponseResource> {

    public Long mapToId(QuestionnaireResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
