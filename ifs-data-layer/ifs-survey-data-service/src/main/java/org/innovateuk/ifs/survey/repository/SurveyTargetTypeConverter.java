package org.innovateuk.ifs.survey.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.survey.SurveyTargetType;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class SurveyTargetTypeConverter extends IdentifiableEnumConverter<SurveyTargetType> {

    public SurveyTargetTypeConverter() {
        super(SurveyTargetType.class);
    }

    @Override
    public Long convertToDatabaseColumn(SurveyTargetType attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public SurveyTargetType convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}