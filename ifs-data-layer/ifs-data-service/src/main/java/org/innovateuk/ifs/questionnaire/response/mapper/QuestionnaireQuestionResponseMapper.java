package org.innovateuk.ifs.questionnaire.response.mapper;


import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.questionnaire.config.mapper.QuestionnaireOptionMapper;
import org.innovateuk.ifs.questionnaire.config.mapper.QuestionnaireQuestionMapper;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                QuestionnaireResponseMapper.class,
                QuestionnaireQuestionMapper.class,
                QuestionnaireOptionMapper.class
        }
)
public abstract class QuestionnaireQuestionResponseMapper extends BaseResourceMapper<QuestionnaireQuestionResponse, QuestionnaireQuestionResponseResource> {


    @Mappings({
            @Mapping(source = "option.question.id", target = "question"),
    })
    public abstract QuestionnaireQuestionResponseResource mapToResource(QuestionnaireQuestionResponse domain);

    public Long mapToId(QuestionnaireQuestionResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
