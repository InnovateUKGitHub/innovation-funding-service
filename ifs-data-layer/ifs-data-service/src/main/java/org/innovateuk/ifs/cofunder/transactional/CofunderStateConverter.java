package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link CofunderState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class CofunderStateConverter extends IdentifiableEnumConverter<CofunderState> {

    public CofunderStateConverter() {
        super(CofunderState.class);
    }

    @Override
    public Long convertToDatabaseColumn(CofunderState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public CofunderState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}