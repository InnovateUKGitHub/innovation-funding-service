package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class EligibilityStateConverter extends IdentifiableEnumConverter<EligibilityState> {

    public EligibilityStateConverter() {
        super(EligibilityState.class);
    }

    @Override
    public Long convertToDatabaseColumn(EligibilityState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public EligibilityState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}