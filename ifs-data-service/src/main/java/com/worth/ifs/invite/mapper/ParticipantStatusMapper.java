package com.worth.ifs.invite.mapper;

import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link com.worth.ifs.invite.domain.ParticipantStatus}.
 */
@Mapper(
        componentModel = "spring"
)
public interface ParticipantStatusMapper  {

    public abstract ParticipantStatusResource mapToResource(ParticipantStatus domain);

    public abstract ParticipantStatus mapToDomain(ParticipantStatusResource resource);
}
