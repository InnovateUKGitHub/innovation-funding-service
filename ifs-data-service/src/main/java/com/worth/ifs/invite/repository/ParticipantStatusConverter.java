package com.worth.ifs.invite.repository;

import com.worth.ifs.commons.util.enums.IdentifiableEnumConverter;
import com.worth.ifs.invite.domain.ParticipantStatus;

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
