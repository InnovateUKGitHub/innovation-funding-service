package org.innovateuk.ifs.form.domain;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.form.resource.FormInputType;

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
