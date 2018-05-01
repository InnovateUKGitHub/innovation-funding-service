package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link AssessmentState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class AssessmentStateConverter extends IdentifiableEnumConverter<AssessmentState> {

    public AssessmentStateConverter() {
        super(AssessmentState.class);
    }

    @Override
    public Long convertToDatabaseColumn(AssessmentState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public AssessmentState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}