package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class GrowthTableMapper extends BaseResourceMapper<GrowthTable, GrowthTableResource> {
}
