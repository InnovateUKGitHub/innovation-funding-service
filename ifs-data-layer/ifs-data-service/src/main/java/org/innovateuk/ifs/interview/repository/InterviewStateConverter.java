package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.interview.resource.InterviewState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link InterviewState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class InterviewStateConverter extends IdentifiableEnumConverter<InterviewState> {

    public InterviewStateConverter() {
        super(InterviewState.class);
    }

    @Override
    public Long convertToDatabaseColumn(InterviewState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public InterviewState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}