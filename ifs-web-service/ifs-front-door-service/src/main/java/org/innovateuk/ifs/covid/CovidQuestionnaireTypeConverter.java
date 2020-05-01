package org.innovateuk.ifs.covid;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CovidQuestionnaireTypeConverter implements Converter<String, CovidQuestionnaireType> {

    @Override
    public CovidQuestionnaireType convert(String from) {
        return CovidQuestionnaireType.fromUrl(from);
    }
}
