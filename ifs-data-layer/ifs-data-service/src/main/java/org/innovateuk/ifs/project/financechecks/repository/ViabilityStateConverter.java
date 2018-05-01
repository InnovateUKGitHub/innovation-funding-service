package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ViabilityStateConverter extends IdentifiableEnumConverter<ViabilityState> {

    public ViabilityStateConverter() {
        super(ViabilityState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ViabilityState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ViabilityState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}