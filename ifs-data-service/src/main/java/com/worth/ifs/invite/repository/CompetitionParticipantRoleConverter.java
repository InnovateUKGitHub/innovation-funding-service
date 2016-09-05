package com.worth.ifs.invite.repository;

import com.worth.ifs.commons.util.enums.IdentifiableEnumConverter;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link CompetitionParticipantRole} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class CompetitionParticipantRoleConverter extends IdentifiableEnumConverter<CompetitionParticipantRole> {

    public CompetitionParticipantRoleConverter() {
        super(CompetitionParticipantRole.class);
    }
}
