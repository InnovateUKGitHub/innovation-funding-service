package org.innovateuk.ifs.project.projectdetails.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ProjectDetailsState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ProjectDetailsStateConverter extends IdentifiableEnumConverter<ProjectDetailsState> {

    public ProjectDetailsStateConverter() {
        super(ProjectDetailsState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ProjectDetailsState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ProjectDetailsState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}