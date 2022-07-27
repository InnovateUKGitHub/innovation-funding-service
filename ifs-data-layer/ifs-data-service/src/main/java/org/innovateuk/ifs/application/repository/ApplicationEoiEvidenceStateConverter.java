package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ApplicationEoiEvidenceStateConverter extends IdentifiableEnumConverter<ApplicationEoiEvidenceState>  {

    public ApplicationEoiEvidenceStateConverter() {
        super(ApplicationEoiEvidenceState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ApplicationEoiEvidenceState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ApplicationEoiEvidenceState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}
