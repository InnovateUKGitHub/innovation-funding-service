package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link SupporterState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class SupporterStateConverter extends IdentifiableEnumConverter<SupporterState> {

    public SupporterStateConverter() {
        super(SupporterState.class);
    }

    @Override
    public Long convertToDatabaseColumn(SupporterState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public SupporterState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}