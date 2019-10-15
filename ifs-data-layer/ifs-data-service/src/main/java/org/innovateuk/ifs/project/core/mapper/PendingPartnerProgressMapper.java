package org.innovateuk.ifs.project.core.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.core.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class PendingPartnerProgressMapper extends BaseResourceMapper<PendingPartnerProgress, PendingPartnerProgressResource> {
}
