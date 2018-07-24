package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link OrganisationSize} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class OrganisationSizeConverter extends IdentifiableEnumConverter<OrganisationSize> {

    public OrganisationSizeConverter() {
        super(OrganisationSize.class);
    }
}
