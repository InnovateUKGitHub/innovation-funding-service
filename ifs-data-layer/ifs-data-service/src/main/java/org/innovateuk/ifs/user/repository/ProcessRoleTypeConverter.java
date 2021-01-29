package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.user.resource.ProcessRoleType;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ProcessRoleType} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ProcessRoleTypeConverter extends IdentifiableEnumConverter<ProcessRoleType> {

    public ProcessRoleTypeConverter() {
        super(ProcessRoleType.class);
    }
}