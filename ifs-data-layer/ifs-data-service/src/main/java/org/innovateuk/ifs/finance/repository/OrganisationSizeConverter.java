package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ParticipantStatus} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class OrganisationSizeConverter extends IdentifiableEnumConverter<OrganisationSize> {

    public OrganisationSizeConverter() {
        super(OrganisationSize.class);
    }
}
