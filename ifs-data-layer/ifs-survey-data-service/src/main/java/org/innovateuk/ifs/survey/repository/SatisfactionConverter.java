package org.innovateuk.ifs.survey.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.survey.Satisfaction;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class SatisfactionConverter extends IdentifiableEnumConverter<Satisfaction> {

    public SatisfactionConverter() {
        super(Satisfaction.class);
    }

    @Override
    public Long convertToDatabaseColumn(Satisfaction attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public Satisfaction convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}