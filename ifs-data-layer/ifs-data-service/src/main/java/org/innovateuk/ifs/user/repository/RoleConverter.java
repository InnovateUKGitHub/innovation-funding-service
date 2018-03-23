package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.user.resource.Role;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link Role} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class RoleConverter extends IdentifiableEnumConverter<Role> {

    public RoleConverter() {
        super(Role.class);
    }
}