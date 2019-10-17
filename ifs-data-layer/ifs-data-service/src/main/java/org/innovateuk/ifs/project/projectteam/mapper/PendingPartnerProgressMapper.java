package org.innovateuk.ifs.project.projectteam.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class PendingPartnerProgressMapper extends BaseResourceMapper<PendingPartnerProgress, PendingPartnerProgressResource> {
}
