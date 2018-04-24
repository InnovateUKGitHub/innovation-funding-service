package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class InterviewAssignmentStateConverter extends IdentifiableEnumConverter<InterviewAssignmentState> {

    public InterviewAssignmentStateConverter() {
        super(InterviewAssignmentState.class);
    }

    @Override
    public Long convertToDatabaseColumn(InterviewAssignmentState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public InterviewAssignmentState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}