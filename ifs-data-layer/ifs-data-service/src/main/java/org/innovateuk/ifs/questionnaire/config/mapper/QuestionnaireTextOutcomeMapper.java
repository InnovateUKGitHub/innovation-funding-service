package org.innovateuk.ifs.questionnaire.config.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireTextOutcome;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {QuestionnaireOptionMapper.class,
        QuestionnaireMapper.class}
)
public abstract class QuestionnaireTextOutcomeMapper extends BaseResourceMapper<QuestionnaireTextOutcome, QuestionnaireTextOutcomeResource> {

    public Long mapToId(QuestionnaireTextOutcome object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
