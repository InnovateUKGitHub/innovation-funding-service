package org.innovateuk.ifs.survey.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.survey.SurveyType;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class SurveyTypeConverter extends IdentifiableEnumConverter<SurveyType> {

    public SurveyTypeConverter() {
        super(SurveyType.class);
    }

    @Override
    public Long convertToDatabaseColumn(SurveyType attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public SurveyType convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}