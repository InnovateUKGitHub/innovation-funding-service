package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.threads.resource.QueryResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {PostMapper.class}
)
public abstract class QueryMapper extends BaseMapper<Query, QueryResource, Long> {

}
