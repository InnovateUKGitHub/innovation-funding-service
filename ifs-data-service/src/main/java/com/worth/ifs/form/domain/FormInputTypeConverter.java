package com.worth.ifs.form.domain;

import com.worth.ifs.commons.util.enums.IdentifiableEnumConverter;
import com.worth.ifs.form.resource.FormInputType;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link FormInputType} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class FormInputTypeConverter extends IdentifiableEnumConverter<FormInputType> {

    public FormInputTypeConverter() {
        super(FormInputType.class);
    }

    @Override
    public Long convertToDatabaseColumn(FormInputType attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public FormInputType convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}