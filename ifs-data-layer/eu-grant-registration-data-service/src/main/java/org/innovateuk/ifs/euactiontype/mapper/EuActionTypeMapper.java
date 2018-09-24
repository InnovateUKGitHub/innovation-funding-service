package org.innovateuk.ifs.euactiontype.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class EuActionTypeMapper extends BaseMapper<EuActionType, EuActionTypeResource, Long> {
}
