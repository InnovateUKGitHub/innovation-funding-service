package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ApplicationStateConverter extends IdentifiableEnumConverter<ApplicationState> {

    public ApplicationStateConverter() {
        super(ApplicationState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ApplicationState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ApplicationState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}