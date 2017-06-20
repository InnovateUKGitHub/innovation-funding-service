package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link org.innovateuk.ifs.invite.domain.ParticipantStatus}.
 */
@Mapper(
        componentModel = "spring"
)
public interface ParticipantStatusMapper  {

    public abstract ParticipantStatusResource mapToResource(ParticipantStatus domain);

    public abstract ParticipantStatus mapToDomain(ParticipantStatusResource resource);
}
