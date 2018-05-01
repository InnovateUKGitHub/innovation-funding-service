package org.innovateuk.ifs.project.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.resource.ProjectState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ProjectState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ProjectStateConverter extends IdentifiableEnumConverter<ProjectState> {

    public ProjectStateConverter() {
        super(ProjectState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ProjectState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ProjectState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}