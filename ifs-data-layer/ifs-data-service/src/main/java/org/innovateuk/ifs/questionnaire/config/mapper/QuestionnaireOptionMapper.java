package org.innovateuk.ifs.questionnaire.config.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireDecision;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireOption;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = QuestionnaireMapper.class
)
public abstract class QuestionnaireOptionMapper extends BaseResourceMapper<QuestionnaireOption, QuestionnaireOptionResource> {

    public Long mapToId(QuestionnaireOption object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Long mapToId(QuestionnaireDecision object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
