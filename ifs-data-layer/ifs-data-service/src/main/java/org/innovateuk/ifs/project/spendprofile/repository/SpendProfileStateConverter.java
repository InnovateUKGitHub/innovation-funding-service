package org.innovateuk.ifs.project.spendprofile.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link SpendProfileState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class SpendProfileStateConverter extends IdentifiableEnumConverter<SpendProfileState> {

    public SpendProfileStateConverter() {
        super(SpendProfileState.class);
    }

    @Override
    public Long convertToDatabaseColumn(SpendProfileState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public SpendProfileState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}