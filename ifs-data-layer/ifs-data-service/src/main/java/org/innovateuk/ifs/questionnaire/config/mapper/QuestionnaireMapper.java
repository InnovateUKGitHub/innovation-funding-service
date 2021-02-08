package org.innovateuk.ifs.questionnaire.config.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = QuestionnaireQuestionMapper.class
)
public abstract class QuestionnaireMapper extends BaseResourceMapper<Questionnaire, QuestionnaireResource> {

    public Long mapToId(Questionnaire object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
