package org.innovateuk.ifs.questionnaire.config.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {QuestionnaireOptionMapper.class,
        QuestionnaireMapper.class}
)
public abstract class QuestionnaireQuestionMapper extends BaseResourceMapper<QuestionnaireQuestion, QuestionnaireQuestionResource> {

    public Long mapToId(QuestionnaireQuestion object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
