package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.domain.EuActionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class EuActionTypeMapper extends BaseMapper<EuActionType, EuActionTypeResource, Long> {

//    @Mappings({
//            @Mapping(target = "id", ignore = true)
//    })
    @Override
    public abstract EuActionType mapToDomain(EuActionTypeResource resource);

}
