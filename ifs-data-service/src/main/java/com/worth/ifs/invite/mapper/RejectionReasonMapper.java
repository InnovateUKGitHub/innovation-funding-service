package com.worth.ifs.invite.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link com.worth.ifs.invite.domain.RejectionReason}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {}
)
public abstract class RejectionReasonMapper extends BaseMapper<RejectionReason, RejectionReasonResource, Long> {

    @Override
    public abstract RejectionReasonResource mapToResource(RejectionReason domain);
}