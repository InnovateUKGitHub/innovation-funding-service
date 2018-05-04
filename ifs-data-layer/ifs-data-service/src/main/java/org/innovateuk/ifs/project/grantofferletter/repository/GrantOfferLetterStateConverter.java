package org.innovateuk.ifs.project.grantofferletter.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link GrantOfferLetterState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class GrantOfferLetterStateConverter extends IdentifiableEnumConverter<GrantOfferLetterState> {

    public GrantOfferLetterStateConverter() {
        super(GrantOfferLetterState.class);
    }

    @Override
    public Long convertToDatabaseColumn(GrantOfferLetterState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public GrantOfferLetterState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}