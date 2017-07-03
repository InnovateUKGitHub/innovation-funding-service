package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link org.innovateuk.ifs.invite.domain.RejectionReason}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {}
)
public abstract class RejectionReasonMapper extends BaseMapper<RejectionReason, RejectionReasonResource, Long> {

    @Override
    public abstract RejectionReasonResource mapToResource(RejectionReason domain);
}
