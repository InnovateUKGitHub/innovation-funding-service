package org.innovateuk.ifs.questionnaire.response.mapper;


import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.mapper.QuestionnaireMapper;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                QuestionnaireMapper.class,
                QuestionnaireQuestionResponseMapper.class
        }
)
public abstract class QuestionnaireResponseMapper extends BaseResourceMapper<QuestionnaireResponse, QuestionnaireResponseResource> {

    public String mapToId(QuestionnaireResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId().toString();
    }

    public String mapUuidToString(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }

    public UUID mapStringToUuid(String uuid) {
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }
}
