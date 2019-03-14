package org.innovateuk.ifs.granttransfer.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.granttransfer.domain.EuActionType;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class EuActionTypeMapper extends BaseMapper<EuActionType, EuActionTypeResource, Long> {
}
