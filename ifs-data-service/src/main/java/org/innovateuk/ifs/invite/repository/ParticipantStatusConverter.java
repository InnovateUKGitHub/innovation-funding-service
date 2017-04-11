package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ParticipantStatus} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ParticipantStatusConverter extends IdentifiableEnumConverter<ParticipantStatus> {

    public ParticipantStatusConverter() {
        super(ParticipantStatus.class);
    }
}
