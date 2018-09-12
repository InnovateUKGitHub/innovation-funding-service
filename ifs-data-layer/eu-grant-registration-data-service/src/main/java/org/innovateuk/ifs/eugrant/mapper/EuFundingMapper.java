package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.euactiontype.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.eugrant.EuFundingResource;
import org.innovateuk.ifs.eugrant.domain.EuFunding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = {
            EuActionTypeMapper.class,
}
)
public abstract class EuFundingMapper extends BaseMapper<EuFunding, EuFundingResource, Long> {

    @Mappings({
            @Mapping(target = "id",  ignore = true),
            @Mapping(target = "actionType", source = "actionType.id")
    })
    @Override
    public abstract EuFunding mapToDomain(EuFundingResource resource);
}